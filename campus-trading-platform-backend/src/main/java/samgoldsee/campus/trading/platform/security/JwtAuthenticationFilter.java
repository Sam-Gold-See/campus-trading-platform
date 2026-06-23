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
import samgoldsee.campus.trading.platform.constant.MessageConstant;
import samgoldsee.campus.trading.platform.constant.RedisConstant;

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

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.isNotEmpty(jwt) && isTokenBlacklisted(jwt)) {
				log.warn("Token已被加入黑名单，拒绝访问");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						MessageConstant.TOKEN_BLACKLISTED);
				return;
			}

			if (StringUtils.isNotEmpty(jwt) && jwtTokenProvider.validateToken(jwt)) {
				String userId = jwtTokenProvider.getUserIdFromToken(jwt);

				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(
								userId,
								null,
								new ArrayList<>()
						);

				authentication.setDetails(
						new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);

				request.setAttribute(JwtTokenProvider.IDENTIFY, userId);
				request.setAttribute(JwtTokenProvider.AUTHORIZATION, jwt);
			}
		} catch (Exception e) {
			log.error("Could not set user authentication in security context", e);
		}

		filterChain.doFilter(request, response);
	}

	private boolean isTokenBlacklisted(String token) {
		Boolean exists = stringRedisTemplate.hasKey(
				RedisConstant.TOKEN_BLACKLIST_PREFIX + token);
		return Boolean.TRUE.equals(exists);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader(JwtTokenProvider.AUTHORIZATION);
		if (StringUtils.isNotEmpty(bearerToken)) {
			return jwtTokenProvider.resolveToken(bearerToken);
		}
		return null;
	}
}
