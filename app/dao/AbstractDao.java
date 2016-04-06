package dao;

import com.google.inject.Inject;
import models.Organisation;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.UUID;

public abstract class AbstractDao<T> {
    private Class<T> entityClass;

    @Inject
    public JPAApi jpaApi;

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
        jpaApi.em().persist(organisation);
    }

    public void delete(Organisation found){
        jpaApi.em().remove(found);
    }

    public void remove(Organisation found){
        delete(found);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}

