package bean;

import java.sql.*;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.sql.DataSource;

public class ImgDAO implements ImgDAO_Interface {
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private static final String USER = "TEST";
	private static final String PASSWORD = "c83758341";
	//取得上次的的Sequence 再add(Img img)後修改currSeq  原本是寫一個getCurrSeq的方法   但單獨開起連線去讀會讀不到 因為開啟前並無使用sequence
	private int currSeq;
	
	private static final String INSERT_STMT = "INSERT INTO IMG(IMGNO, INAME, IEXP, ITIME, ISIZE, IMG)"
			+ "VALUES(Imgno_Sq.NEXTVAL, ?, ?, current_timestamp, ?, ?)";
	private static final String UPDATE_STMT = "UPDATE IMG SET IMGNO = ?, INAME = ?, IEXP = ?, "
			+ "ITIME = ?,ISIZE=? ,IMG = ?";
	private static final String DELETE_STMT = "DELETE FROM IMG WHERE IMGNO = ?";
	private static final String FIND_BY_PK = "SELECT * FROM IMG WHERE IMGNO = ?";
	private static final String GET_ALL = "SELECT * FROM IMG";
	private static final String GET_CURRSEQ = "select imgno_sq.currval from dual";
	
	
	
	
	@Override
	public void add(Img img) {
		Connection con = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		
		try {

			Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Test");
			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, img.getIname());
			pstmt.setString(2, img.getIexp());
			Blob blob=con.createBlob();
			byte[] b=img.getImg();
			blob.setBytes(1, b);
			pstmt.setInt(3, img.getIsize());
			pstmt.setBlob(4, blob);
			pstmt.executeUpdate();
			
			pstmt2 = con.prepareStatement(GET_CURRSEQ);
			ResultSet rs2=pstmt2.executeQuery();
			rs2.next();
			currSeq = rs2.getInt(1);

		} catch (Exception se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public void update(Img img) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Test");
			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE_STMT);

			pstmt.setInt(1, img.getImgno());
			pstmt.setString(2, img.getIname());
			pstmt.setString(3, img.getIexp());
			pstmt.setTimestamp(4,img.getItime() );
			Blob blob=con.createBlob();
			byte[] b=img.getImg();
			blob.setBytes(1, b);
			pstmt.setInt(5, img.getIsize());
			pstmt.setBlob(6, blob);
			
			pstmt.executeUpdate();


			// Handle any driver errors
		}  catch (Exception se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public void delete(int imgno) {
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Test");
			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE_STMT);

			pstmt.setInt(1, imgno);
			
			pstmt.executeUpdate();

		} catch (Exception se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		
	}

	@Override
	public Img findByPk(int imgno) {
		Img img = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			Context ctx = new javax.naming.InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/Test");
			con = ds.getConnection();
			pstmt = con.prepareStatement(FIND_BY_PK);
			pstmt.setInt(1, imgno);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				img = new Img();
				img.setImgno(rs.getInt("IMGNO"));
				img.setIname(rs.getString("INAME"));
				img.setIexp(rs.getString("IEXP"));
				img.setItime(rs.getTimestamp("ITIME"));
				img.setIsize(rs.getInt("ISIZE"));
				img.setImg(rs.getBytes("IMG"));
			
			}

		}  catch (Exception se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

		return img;
	}

	@Override
	public List<Img> getAll() {
		return null;
	}

	public int getCurrSeq(){
		return currSeq;
	}

	
	
	//錯誤寫法  blob回傳不了 連線已關
//	public Blob getBlob(){
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		Blob blob;
//		
//		try {
//
//			Class.forName(DRIVER);
//			con = DriverManager.getConnection(URL, USER, PASSWORD);
//			blob=con.createBlob();
//
//	
//		} catch (ClassNotFoundException ce) {
//			throw new RuntimeException("Couldn't load database driver. " + ce.getMessage());
//		} catch (SQLException se) {
//			throw new RuntimeException("A database error occured. " + se.getMessage());
//		} finally {
//			if (pstmt != null) {
//				try {
//					pstmt.close();
//				} catch (SQLException se) {
//					se.printStackTrace(System.err);
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (Exception e) {
//					e.printStackTrace(System.err);
//				}
//			}
//		}
//		return blob;
//	}
	

}
