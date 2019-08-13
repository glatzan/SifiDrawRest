package eu.glatz.sifidraw.config

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import java.io.IOException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.*


/**
 * CORS filter for http-request and response
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CORSFilter : Filter {

    /**
     * Do Filter on every http-request.
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        val request = req as HttpServletRequest
        response.setHeader("Access-Control-Allow-Origin", "*")
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "access_token, authorization, content-type")

        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
        } else {
            chain.doFilter(req, res)
        }
    }

    /**
     * Destroy method
     */
    override fun destroy() {}

    /**
     * Initialize CORS filter
     */
    @Throws(ServletException::class)
    override fun init(arg0: FilterConfig) {
    }
}
