package bean;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Timestamp;


public class Img implements Serializable {
	private int imgno;
	private String iname;
	private String iexp;
	private Timestamp itime;
	private int isize;
	private byte[] img;
	
	
	public Img(){};
	
	public Img(int imgno,String iname,String iexp,Timestamp itime,int isize,byte[] img){
		this.imgno=imgno;
		this.iname=iname;
		this.iexp=iexp;
		this.itime=itime;
		this.isize=isize;
		this.img=img;
	}

	public int getImgno() {
		return imgno;
	}

	public void setImgno(int imgno) {
		this.imgno = imgno;
	}

	public String getIname() {
		return iname;
	}

	public void setIname(String iname) {
		this.iname = iname;
	}

	public String getIexp() {
		return iexp;
	}

	public void setIexp(String iexp) {
		this.iexp = iexp;
	}

	public Timestamp getItime() {
		return itime;
	}

	public void setItime(Timestamp itime) {
		this.itime = itime;
	}

	public byte[] getImg() {
		return img;
	}

	public void setImg(byte[] img) {
		this.img = img;
	}

	public int getIsize() {
		return isize;
	}

	public void setIsize(int isize) {
		this.isize = isize;
	}


}
