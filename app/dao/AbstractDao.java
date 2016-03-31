package dao;

import play.db.jpa.JPA;

import java.util.List;
import java.util.UUID;

public abstract class AbstractDao<T> {
    private Class<T> entityClass;


    public AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll() {
        String sql = "select e from " + entityClass.getSimpleName() + " e ";
        return JPA.em()
                .createQuery(sql, entityClass)
                .getResultList();
    }

    public T findById(UUID id) {
        return JPA.em().find(entityClass, id);
    }

}
