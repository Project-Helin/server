package dao;

import com.google.inject.Inject;
import models.Organisation;
import play.db.jpa.JPA;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.UUID;

public abstract class AbstractDao<T> {
    private Class<T> entityClass;

    @Inject
    private JPAApi jpaApi;

    public AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        String sql = "select e from " + entityClass.getSimpleName() + " e ";
        return jpaApi.em()
                .createQuery(sql, entityClass)
                .getResultList();
    }

    public T findById(UUID id) {
        return jpaApi.em().find(entityClass, id);
    }

    public void persist(Organisation organisation){
        // JPA.em().merge(organisation);
        jpaApi.em().persist(organisation);
    }
}

