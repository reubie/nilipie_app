package com.mobipesa.nilipieapp.models

import com.google.gson.annotations.SerializedName

data class DependantItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("upperLimit")
	val upperLimit: Int? = null,

	@field:SerializedName("limitBalance")
	val limitBalance: Int? = null
)