package dimpesh.com.nearbyeasy;

import android.os.Parcel;
import android.os.Parcelable;

public class MyObject implements Parcelable {
	String name,vicinity,id;
	String icon;
	String rating;

	protected MyObject(Parcel in) {
		name = in.readString();
		vicinity = in.readString();
		id = in.readString();
		icon = in.readString();
		rating = in.readString();
	}

	public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
		@Override
		public MyObject createFromParcel(Parcel in) {
			return new MyObject(in);
		}

		@Override
		public MyObject[] newArray(int size) {
			return new MyObject[size];
		}
	};

	public MyObject() {

	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(vicinity);
		dest.writeString(id);
		dest.writeString(icon);
		dest.writeString(rating);
	}
}
