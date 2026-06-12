package samgoldsee.campus.trading.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author HuangChunXin
 * @date 2026/5/22 10:45
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final StringRedisTemplate stringRedisTemplate;

	private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

	/**
	 * 过滤器核心逻辑
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		try {
			// 从请求中提取JWT Token
			String jwt = getJwtFromRequest(request);

			// 检查Token是否在黑名单中
			if (StringUtils.isNotEmpty(jwt) && isTokenBlacklisted(jwt)) {
				log.warn("Token已被加入黑名单，拒绝访问");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token已失效，请重新登录");
				return;
			}

			// 如果Token存在且有效，则设置认证消息
			if (StringUtils.isNotEmpty(jwt) && jwtTokenProvider.validateToken(jwt)) {
				// 从Token中获取用户ID
				String userId = jwtTokenProvider.getUserIdFromToken(jwt);

				// 创建认证对象
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(
								userId,  // principal - 用户标识
								null,    // credentials - 凭证(不需要)
								new ArrayList<>()  // authorities - 权限列表(暂时为空)
						);

				// 设置请求详情
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// 将认证信息设置到Security上下文
				SecurityContextHolder.getContext().setAuthentication(authentication);

				// 将userId作为请求属性传递，方便后续使用
				request.setAttribute(JwtTokenProvider.IDENTIFY, userId);

				// 将原始Token也保存到请求属性
				request.setAttribute(JwtTokenProvider.AUTHORIZATION, jwt);
			}
		} catch (Exception e) {
			log.error("Could not set user authentication in security context", e);
		}

		// 继续过滤器链
		filterChain.doFilter(request, response);
	}

	/**
	 * 检查Token是否在黑名单中
	 */
	private boolean isTokenBlacklisted(String token) {
		Boolean exists = stringRedisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token);
		return Boolean.TRUE.equals(exists);
	}

	/**
	 * 从HTTP请求中提取JWT Token
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		// 从Authorization头获取Token
		String bearerToken = request.getHeader(JwtTokenProvider.AUTHORIZATION);

		// 解析Bearer Token
		if (StringUtils.isNotEmpty(bearerToken)) {
			return jwtTokenProvider.resolveToken(bearerToken);
		}

		return null;
	}
}
