package com.mobipesa.nilipieapp.models


import com.google.gson.annotations.SerializedName


data class UsersItem(

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("wallet")
	val wallet: Int? = null,

	@field:SerializedName("msisdn")
	val msisdn: String? = null
)