package com.example.profilecompose

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ProfileSerializer : Serializer<Profile> {
    override val defaultValue: Profile
        get() = Profile.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Profile {
        try {
            return Profile.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto, ", e)
        }
    }

    override suspend fun writeTo(t: Profile, output: OutputStream) = t.writeTo(output)
}