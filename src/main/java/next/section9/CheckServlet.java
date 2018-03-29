package next.section9;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CheckServlet  extends HttpServlet {

    CheckService checkService;

    public CheckService getCheckService() {
        if(checkService == null ){
            checkService = new CheckService();
        }
        return checkService;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException
    {

    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        String conditionString = request.getParameter("condition");
        Map<String,String> condition = JSON.parseObject(conditionString,Map.class);
        boolean result = getCheckService().check(condition);
        PrintWriter out = response.getWriter();
        try {
            out.println(result);
        } finally {
            out.close();
        }
    }
}
