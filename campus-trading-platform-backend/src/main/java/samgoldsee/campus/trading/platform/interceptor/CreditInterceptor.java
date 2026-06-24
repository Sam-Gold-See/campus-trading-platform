package samgoldsee.campus.trading.platform.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import samgoldsee.campus.trading.platform.constant.CreditConstant;
import samgoldsee.campus.trading.platform.exception.BusinessException;
import samgoldsee.campus.trading.platform.mapper.UserMapper;

/**
 * 信用分拦截器
 * 拦截发布操作，检查用户信用分
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Component
@RequiredArgsConstructor
public class CreditInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    // 信用分缓存key前缀
    private static final String CREDIT_CACHE_PREFIX = "user:credit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前用户ID
        String userIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userIdStr == null) {
            throw new BusinessException(401, "未登录");
        }

        Long userId = Long.valueOf(userIdStr);

        // 从缓存获取信用分
        String cacheKey = CREDIT_CACHE_PREFIX + userId;
        String cachedScore = redisTemplate.opsForValue().get(cacheKey);

        int creditScore;
        if (cachedScore != null) {
            creditScore = Integer.parseInt(cachedScore);
        } else {
            // 从数据库查询
            var user = userMapper.findById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            creditScore = user.getCreditScore();
            // 缓存信用分（有效期1小时）
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(creditScore), 3600, java.util.concurrent.TimeUnit.SECONDS);
        }

        // 检查信用分是否低于阈值
        if (creditScore < CreditConstant.MIN_THRESHOLD) {
            throw new BusinessException(403, "信用分低于" + CreditConstant.MIN_THRESHOLD + "分，已被限制发布权限");
        }

        return true;
    }
}