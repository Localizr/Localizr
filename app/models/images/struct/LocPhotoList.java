package models.images.struct;

import java.util.ArrayList;
import java.util.List;

public class LocPhotoList {
	
	private List<LocPhoto> listPhotos = new ArrayList<LocPhoto>();
	
	public void addPhoto(LocPhoto photo){
		if(!photo.skipPhotoDueToRatio())
			listPhotos.add(photo);
	}
	
	public List<LocPhoto> getPhotos(){
		return listPhotos;
	}

}
