package com.bagoy.mydicodingapp.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Response(
	@field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem> = listOf(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListEventsItem(
	@field:SerializedName("id")
	val id: Int = 0,

	@field:SerializedName("name")
	val name: String = "",

	@field:SerializedName("summary")
	val summary: String = "",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("imageLogo")
	val imageLogo: String = "",

	@field:SerializedName("mediaCover")
	val mediaCover: String = "",

	@field:SerializedName("category")
	val category: String = "",

	@field:SerializedName("ownerName")
	val ownerName: String = "",

	@field:SerializedName("cityName")
	val cityName: String = "",

	@field:SerializedName("quota")
	val quota: Int = 0,

	@field:SerializedName("registrants")
	val registrants: Int = 0,

	@field:SerializedName("beginTime")
	val beginTime: String = "",

	@field:SerializedName("endTime")
	val endTime: String = "",

	@field:SerializedName("link")
	val link: String = ""
) : Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readInt(),
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readInt(),
		parcel.readInt(),
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readString() ?: ""
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(id)
		parcel.writeString(name)
		parcel.writeString(summary)
		parcel.writeString(description)
		parcel.writeString(imageLogo)
		parcel.writeString(mediaCover)
		parcel.writeString(category)
		parcel.writeString(ownerName)
		parcel.writeString(cityName)
		parcel.writeInt(quota)
		parcel.writeInt(registrants)
		parcel.writeString(beginTime)
		parcel.writeString(endTime)
		parcel.writeString(link)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListEventsItem> {
		override fun createFromParcel(parcel: Parcel): ListEventsItem {
			return ListEventsItem(parcel)
		}

		override fun newArray(size: Int): Array<ListEventsItem?> {
			return arrayOfNulls(size)
		}
	}
}
