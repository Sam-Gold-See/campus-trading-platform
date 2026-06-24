package samgoldsee.campus.trading.platform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import samgoldsee.campus.trading.platform.interceptor.CreditInterceptor;

/**
 * Web配置
 * 配置信用分拦截器
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CreditInterceptor creditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截发布物品接口
        registry.addInterceptor(creditInterceptor)
                .addPathPatterns("/api/item/publish");
    }
}