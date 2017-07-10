package control;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.*;
import javax.servlet.http.*;

import bean.Img;
import bean.ImgDAO;

import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;

@WebServlet("/uploadServlet4.do")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024 * 1024, maxRequestSize = 5 * 5 * 1024
		* 1024)

public class UploadTest_Servlet4 extends HttpServlet {
	private static final long serialVersionUID = 1L;
	int lastImgSq = 0;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("Big5");
		HttpSession session = req.getSession();
		res.setContentType("text/html; charset=Big5");
		PrintWriter out = res.getWriter();
		// ���s���֨�
		res.setHeader("Cache-Control", "no-store");
		res.setHeader("Pragma", "no-cache");
		res.setDateHeader("Expires", 0);

		ImgDAO dao = new ImgDAO();

		// list�ΨӦs��ID �A���session�̶ǻ�
		List<Integer> list = new ArrayList<Integer>();

		out.println("<HTML>");
		out.println("<HEAD><TITLE>Hello</TITLE></HEAD>");
		out.println("<BODY>");
		out.write("<FORM METHOD=\"get\" ACTION=\"uploadServlet4.do\">");

		Collection<Part> parts = req.getParts();
		System.out.println(parts);
		System.out.println(parts==null);
		for (Part part : parts) {
			System.out.println("-----------------------------");
			if (getFileNameFromPart(part) != null && part.getContentType() != null) {
				byte[] b = getPictureByteArray(part.getInputStream());
				Timestamp dummytime = new Timestamp(System.currentTimeMillis());
				String fileName = getFileNameFromPart(part);
				long size = part.getSize();
				int kbSize = (int) (size / 1024);
				Img img = new Img(1, fileName, "Test", dummytime, kbSize, b);
				// �s�W�Ӥ�
				dao.add(img);
				// ���o�̪�s�W�Ӥ���id
				int currSeq = dao.getCurrSeq();
				// �p�G�W�Ǫ��O�v������ ��video  //�b���~ �e�ݹw���S���P�_
				if (part.getContentType().substring(0, 5).equals("video")) {
					out.println("<input type=\"checkbox\" name=\"delete_fileId\" value=\"" + currSeq + "\">" + "�R��"
							+ "<br>");
					out.print("<video controls poster=\"\">");
					out.print(" <source src=\"DisplayImg?imgno=" + currSeq + "\" type=\"" + part.getContentType()
							+ "\">");
					out.print("</video>" + "<br>");
				} else {
					out.println("<input type=\"checkbox\" name=\"delete_fileId\" value=\"" + currSeq + "\">" + "�R��"
							+ "<br>");
					out.println("<img src=\"DisplayImg?imgno=" + currSeq + "\" width=\"400px\" height=\"300px\"><br>");
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa");
				out.println("<p>�ɮצW�� : " + fileName + "<br>�W�Ǯɶ� : " + sdf.format(System.currentTimeMillis())
						+ "<br>�ɮפj�p : " + kbSize + "KB<br>" + "</p>");
				list.add(currSeq);
			}
		}

		session.setAttribute("id", list);

		out.write("<INPUT TYPE=\"SUBMIT\" VALUE=\"�R��\">");
		out.write("</FORM>");
		out.println("</BODY>");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setCharacterEncoding("Big5"); // �B�z�����ɦW
		res.setContentType("text/html; charset=Big5");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();
		// ���X�����W�Ǫ��ɮ�id
		ArrayList<Integer> list = (ArrayList<Integer>) session.getAttribute("id");
		ImgDAO dao = new ImgDAO();


		out.println("<HTML>");
		out.println("<HEAD><TITLE>Hello</TITLE></HEAD>");
		out.println("<BODY>");

		// ���X�n�R���ɮת�id
		String deleted_files[] = req.getParameterValues("delete_fileId");

		if (deleted_files != null) {
			out.println("�R��" + deleted_files.length + "���ɮ�");
			// �R���ɮ�
			for (int i = 0; i < deleted_files.length; i++) {
				Integer j = Integer.parseInt(deleted_files[i]);
				int deletedID = j.intValue();
				// �R����db�O�o���K��list�̪�id�]����
				list.remove(j);
				dao.delete(deletedID);
			}
		} else {
			out.println("���R�������ɮ�");
		}

		out.write("<FORM METHOD=\"get\" ACTION=\"uploadServlet4.do\">");

		// �⥼�R�����Ӥ��q�X��
		for (Integer id : list) {
			out.println("<input type=\"checkbox\" name=\"delete_fileId\" value=\"" + id + "\">" + "�R��" + "<br>");
			out.println("<img src=\"DisplayImg?imgno=" + id + "\" width=\"400px\" height=\"300px\"><br>");

			Img img = dao.findByPk(id);
			String fileName = img.getIname();
			Timestamp time = img.getItime();
			System.out.println("Time: "+time);
			long dd=time.getTime();
			int kbSize = img.getIsize();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa");
			out.println("<p>�ɮצW�� : " + fileName + "<br>�W�Ǯɶ� : " + sdf.format(dd) + "<br>�ɮפj�p : " + kbSize + "KB<br>"
					+ "</p>");

		}

		// �p�G�ɮץ��R�� ���ɨ쭺��
		if (list.size() == 0) {
			res.sendRedirect("/Album/Album.html");
		}

		out.write("<INPUT TYPE=\"SUBMIT\" VALUE=\"�R��\">");
		out.write("</FORM>");
		out.println("</BODY></HTML>");
	}

	// ���X�W�Ǫ��ɮצW�� (�]��API������method,�ҥH�����ۦ漶�g)
	public String getFileNameFromPart(Part part) {
		String header = part.getHeader("content-disposition");
		String filename = new File(header.substring(header.lastIndexOf("=") + 2, header.length() - 1)).getName();
		if (filename.length() == 0) {
			return null;
		}
		return filename;
	}

	public static byte[] getPictureByteArray(InputStream fis) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int i;
		while ((i = fis.read(buffer)) != -1) {
			baos.write(buffer, 0, i);
		}
		baos.close();
		fis.close();

		return baos.toByteArray();
	}

}