package ru.alesandrus.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.alesandrus.models.AdOwner;

/**
 * @author Alexander Ivanov
 * @version 1.0
 * @since 17.01.2019
 */
public interface AdOwnerRepository extends CrudRepository<AdOwner, Long> {
}
