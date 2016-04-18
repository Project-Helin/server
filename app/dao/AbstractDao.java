package dao;

import com.google.inject.Inject;
import models.BaseEntity;
import play.db.jpa.JPAApi;

import java.util.List;
import java.util.UUID;

public abstract class AbstractDao<T extends BaseEntity> {
    private Class<T> entityClass;

    @Inject
    protected JPAApi jpaApi;

    public AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        String sql = "select e from " + entityClass.getSimpleName() + " e order by e.id";
        return jpaApi.em()
                .createQuery(sql, entityClass)
                .getResultList();
    }

    public T findById(UUID id) {
        return jpaApi.em().find(entityClass, id);
    }

    public void persist(T entity) {
        jpaApi.em().persist(entity);
    }

    public void delete(T entity) {
        jpaApi.em().remove(entity);
    }

    public void remove(T entity) {
        delete(entity);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }


}

