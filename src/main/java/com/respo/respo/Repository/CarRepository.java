package com.respo.respo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.respo.respo.Entity.CarEntity;
import java.util.List; // Add this import
import org.springframework.data.jpa.repository.Query; // Import for @Query
import org.springframework.data.repository.query.Param; // Import for @Param
@Repository
public interface CarRepository extends JpaRepository<CarEntity, Integer>{
    // Explicitly define the query using JPQL if necessary
    @Query("SELECT c FROM CarEntity c WHERE c.owner.userId = :ownerId")
    List<CarEntity> findByOwnerId(@Param("ownerId") int ownerId);
    
    @Query("SELECT c FROM CarEntity c JOIN FETCH c.owner")
    List<CarEntity> findAllWithOwner();
    
    List<CarEntity> findAllByIsRented(boolean isRented);


}
