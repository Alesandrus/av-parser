package ru.alesandrus.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.alesandrus.models.Advertisement;

import java.util.Optional;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 13.01.2019
 */
public interface AdvertisementRepository extends CrudRepository<Advertisement, Long> {
    Optional<Advertisement> findByUrl(String url);
}
