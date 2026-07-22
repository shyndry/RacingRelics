package control;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Filter per garantire la piena compatibilità con il protocollo HTTPS e
 * con Reverse Proxy (Cloudflare, Nginx, AWS ALB, Heroku, Render, ecc.).
 * Gestisce le intestazioni X-Forwarded-Proto/Ssl, forza lo schema https nei redirect
 * e imposta le intestazioni di sicurezza HTTPS (HSTS, Anti-Sniff, Referrer-Policy).
 */
@WebFilter("/*")
public class HttpsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // Verfica se la richiesta proviene da HTTPS (diretta o tramite Reverse Proxy)
        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        String forwardedSsl = req.getHeader("X-Forwarded-Ssl");
        String frontEndHttps = req.getHeader("Front-End-Https");
        
        boolean isHttps = req.isSecure() 
                || "https".equalsIgnoreCase(forwardedProto) 
                || "on".equalsIgnoreCase(forwardedSsl)
                || "on".equalsIgnoreCase(frontEndHttps);

        // Header di sicurezza HTTPS e HSTS
        if (isHttps) {
            resp.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        }
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Se la connessione è HTTPS ma il container la legge come HTTP (dietro proxy), incapsuliamo la richiesta
        HttpServletRequest wrappedReq = req;
        if (isHttps && !req.isSecure()) {
            wrappedReq = new HttpServletRequestWrapper(req) {
                @Override
                public String getScheme() {
                    return "https";
                }

                @Override
                public boolean isSecure() {
                    return true;
                }

                @Override
                public int getServerPort() {
                    String forwardedPort = req.getHeader("X-Forwarded-Port");
                    if (forwardedPort != null && !forwardedPort.trim().isEmpty()) {
                        try {
                            return Integer.parseInt(forwardedPort.trim());
                        } catch (NumberFormatException ignored) {}
                    }
                    return 443;
                }

                @Override
                public StringBuffer getRequestURL() {
                    StringBuffer url = new StringBuffer();
                    url.append("https://").append(getServerName());
                    int port = getServerPort();
                    if (port != 80 && port != 443) {
                        url.append(":").append(port);
                    }
                    url.append(getRequestURI());
                    return url;
                }
            };
        }

        // Incapsuliamo la risposta per convertire eventuali redirect HTTP in HTTPS
        final boolean httpsActive = isHttps;
        HttpServletResponse wrappedResp = new HttpServletResponseWrapper(resp) {
            @Override
            public void sendRedirect(String location) throws IOException {
                if (httpsActive && location != null && location.startsWith("http://")) {
                    location = "https://" + location.substring(7);
                }
                super.sendRedirect(location);
            }
        };

        chain.doFilter(wrappedReq, wrappedResp);
    }
}
