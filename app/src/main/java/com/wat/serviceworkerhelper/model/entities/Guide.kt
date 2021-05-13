package com.wat.serviceworkerhelper.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wat.serviceworkerhelper.utils.Determiners
import java.io.Serializable

@Entity(tableName = "guides_table")
data class Guide(

    @PrimaryKey
    @ColumnInfo(name = "uid")
    var uid: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "steps")
    var steps: ArrayList<Step> = ArrayList(),

    @ColumnInfo(name = "tags")
    var tags: ArrayList<String> = ArrayList(),

    @ColumnInfo(name = "opinions")
    var opinions: HashMap<String, Opinion> = HashMap(),

    @ColumnInfo(name = "rate")
    var rate: Float = -1F,

    @ColumnInfo(name = "opinionsStats")
    var opinionsStats: HashMap<String, Int> = HashMap(),

    @ColumnInfo(name = "creatorUID")
    var creatorUID: String = "",

    @ColumnInfo(name = "creationDate")
    var creationDate: String = "",

    @ColumnInfo(name = "guideStatus")
    var guideStatus: Status = Status.ADDED

) : Serializable {

    /**
     * Step class is responsible for storing information about single step.
     *
     * @param content Contains the content of the step
     * @param photoUrl Contains the photo Url of step
     * (app store all photos in firebase and have access to it via url)
     */
    data class Step(
        var content: String = "",
        var photoUrl: String = ""
    ) : Serializable

    /**
     * Status class is responsible for granting the guide status
     *
     * @property PENDING This status says that guide is currently under review by admins and
     * waits for acceptance
     * @property REPORTED This status says that guide is currently reported and waits for admin review
     * @property ADDED This status says that guide is added and visible for all users
     */
    enum class Status {
        PENDING,
        REPORTED,
        ADDED
    }

    /**
     * Opinion class is responsible for storing information about single opinion
     *
     * @param creatorUID Contains information about creator UID, it is need to show who added opinion
     * @param opinion Contains content of opinion
     * @param rate Contains information about given rate
     * @param date Contains information about date when opinion has been added
     */
    data class Opinion(
        var creatorUID: String = "",
        var opinion: String = "",
        var rate: Float = -1F,
        var date: String = ""
    ) : Serializable {

        /**
         * @return Returns specific String that contains all information about opinion separated by
         * specific determiner. It is need for converting custom class to basic types to save it in database.
         */
        override fun toString(): String {
            return "$determiner$creatorUID" +
                    "$determiner$opinion" +
                    "$determiner$rate" +
                    "$determiner$date"
        }

        companion object {

            private val determiner = Determiners.VALUES.determiner

            /**
             * @param value It is string that describes object of opinion class.
             * @return Returns converted value from string to Opinion
             */
            fun toOpinion(value: String): Opinion {
                val temp = value
                    .substring(1)
                    .split(determiner)
                    .toTypedArray()

                return Opinion(temp[0], temp[1], temp[2].toFloat(), temp[3])
            }
        }
    }
}

