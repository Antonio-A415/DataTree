package com.datatree.infraestructure.utils

class sdk_version {


    companion object {
        const val SDK_VERSION = 33
        const val SDK_VERSION_NAME = "Tiramisu"
        const val SDK_VERSION_CODE = 13
        const val SDK_VERSION_NAME_CODE = "Tiramisu"
        const val SDK_VERSION_NAME_CODE_2 = "Tiramisu"
        const val SDK_VERSION_NAME_CODE_3 = "Tiramisu"


        fun validateSdkVersion(): Boolean {
            return SDK_VERSION >= 33
        }
    }
}