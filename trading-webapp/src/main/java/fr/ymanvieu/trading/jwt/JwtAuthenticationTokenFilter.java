/**
 * Copyright (C) 2019 Yoann Manvieu
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ymanvieu.trading.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final String tokenHeader;

    public JwtAuthenticationTokenFilter(JwtTokenUtil jwtTokenUtil, String tokenHeader) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.tokenHeader = tokenHeader;
	}
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String requestHeader = request.getHeader(tokenHeader);
        
        log.debug("filtered URL: {}", request.getRequestURL());

        String username = null;
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                log.error("an error occured during getting username from token", e);
            } catch (JwtException e) {
            	log.info("Security exception - {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
        	log.debug("couldn't find bearer string, will ignore the header");
        }

        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        	log.debug("checking authentication for user " + username);

            // It is not compelling necessary to load the user details from the database. 
        	// You could also store the information in the token and read it from it. It's up to you ;)
        	User userDetails = new User(username, "", jwtTokenUtil.getGrantedAuthoritiesFromToken(authToken));

            // For simple validation it is completely sufficient to just check the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                log.debug("authenticated user " + username + ", setting security context");
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}