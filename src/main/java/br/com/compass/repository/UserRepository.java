package br.com.compass.repository;

import br.com.compass.model.entity.User;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class UserRepository extends RepositoryFactory<User> {

    public UserRepository() {
        super();
    }

    public void save(User user) {
        super.save(user);
    }

    public Optional<User> findByCpf(String cpf) {
        try {
            TypedQuery<User> query = getEntityManager().createQuery(
                    "SELECT u FROM User u WHERE u.cpf = :cpf", User.class);
            query.setParameter("cpf", cpf);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


}
