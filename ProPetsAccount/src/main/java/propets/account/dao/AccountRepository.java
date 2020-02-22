package propets.account.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import propets.account.domain.User;

public interface AccountRepository extends MongoRepository<User, String> {

}
