package com.example.uts_a22202302996.profile;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import androidx.room.Query;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Profile profile);

    // Perbaikan: Ganti 'user' menjadi 'profiles'
    @Query("DELETE FROM user WHERE username = :username")
    void delete(String username);

    // Perbaikan: Ganti 'user' menjadi 'profiles'
    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    LiveData<Profile> getProfile(String username);

    // Perbaikan: Ganti 'user' menjadi 'profiles'
    @Query("SELECT lastUpdated FROM user WHERE username = :username")
    long getLastUpdated(String username);
}
