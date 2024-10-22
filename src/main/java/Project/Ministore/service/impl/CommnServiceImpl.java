package Project.Ministore.service.impl;

import Project.Ministore.service.CommnService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Service
public class CommnServiceImpl implements CommnService {

    @Override
    public void removeSessionMessage() {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes()))
                    .getRequest();
            HttpSession session = request.getSession();
            session.removeAttribute("succMsg");
            session.removeAttribute("errorMsg");
    }
}
