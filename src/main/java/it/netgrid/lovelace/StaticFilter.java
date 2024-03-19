package it.netgrid.lovelace;

import jakarta.inject.Singleton;
import jakarta.servlet.*;
import java.io.IOException;
@Singleton
public class StaticFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(request, response);
    }
}
