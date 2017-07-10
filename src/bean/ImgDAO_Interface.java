package bean;

import java.util.List;

public interface ImgDAO_Interface {
	void add(Img img);
	void update(Img img);
	void delete(int imgno);
	Img findByPk(int imgno);
	List<Img> getAll();
}
