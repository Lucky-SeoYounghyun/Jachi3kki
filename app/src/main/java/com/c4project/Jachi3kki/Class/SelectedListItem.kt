package com.c4project.Jachi3kki.Class

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Fragment 간 데이터 전달시 사용되는 자료형

@Parcelize
data class SelectedListItem(val data: String) : Parcelable

@Parcelize
class SelectedItems : ArrayList<SelectedListItem>(), Parcelable