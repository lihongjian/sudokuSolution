package next.section9;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CalculateServlet extends HttpServlet {

    private static final long serialVersionUID = -1915463532411657451L;

    CalculateService calculateService = null;

    public CalculateService getCalculateService() {
        if (calculateService == null) {
            synchronized (this) {
                if (calculateService == null) {
                    calculateService = new CalculateService();
                }
            }
        }
        return calculateService;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)

            throws ServletException, IOException {

        response.setContentType("application/json;charset=utf-8");
        String conditionString = request.getParameter("condition");
        Map<String, String> condition = JSON.parseObject(conditionString, Map.class);
        Map<String, String> resultMap = null;
        try {
            long a1 = System.currentTimeMillis();
            resultMap = getCalculateService().calculate(condition);
            long a2 = System.currentTimeMillis();
            System.out.println("Cost time : " + (a2-a1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = JSON.toJSONString(resultMap);
        PrintWriter out = response.getWriter();
        try {
            out.println(result);
        } finally {
            out.close();
        }
    }

}
