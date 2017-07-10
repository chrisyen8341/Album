package control;



import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import bean.Img;
import bean.ImgDAO;


@WebServlet("/DisplayImg")
public class DisplayImg extends HttpServlet {
    private static final long serialVersionUID = 1L;


  //有id  去資料庫抓blob  透過ServletOutputStream將byte陣列秀出圖片
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String imgno2 = request.getParameter("imgno");
		Integer imgno1=Integer.parseInt(imgno2);
		int imgno=imgno1.intValue();
//		response.setBufferSize(1024*1024);
	    ServletOutputStream out = response.getOutputStream();
	    ImgDAO dao=new ImgDAO();

	    if (imgno2 ==null) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND); 
	        return;
	    }

	    try{
			Img img=dao.findByPk(imgno);
			byte[] b=img.getImg();
	        out.write(b);
	    } catch (Exception e){
	        e.printStackTrace();
	    }
	}
	

}
