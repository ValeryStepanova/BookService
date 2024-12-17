package com.ifortex.bookservice.repository.impl;

import com.ifortex.bookservice.dto.SearchCriteria;
import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final EntityManagerFactory entityManagerFactory;

    public BookRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Map<String, Long> getCountOFBooks() {
        List<String> genres = getUniqueGenres();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Map<String, Long> map = new HashMap<>();
        try {
            for (String genre : genres) {
                String sql = "SELECT COUNT(*) AS total_books FROM books WHERE ? = ANY (genre) ORDER BY total_books desc"; //doesnt sort
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter(1, genre);
                Long count = ((Number) query.getSingleResult()).longValue();
                map.put(genre, count);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        } finally {
            entityManager.close();
        }
        return map.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (count1, count2) -> count1,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<String> getUniqueGenres() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = null;
        entityManager.getTransaction().begin();
        try {
            String sql = "SELECT DISTINCT unnest(genre) AS genre FROM books";
            query = entityManager.createNativeQuery(sql);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        return query.getResultList();
    }

    @Override
    public List<Book> findByCriteria(SearchCriteria searchCriteria) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);
        List<Predicate> predicates = new ArrayList<>();
        if (searchCriteria.getTitle() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + searchCriteria.getTitle().toLowerCase() + "%"));
        }
        if (searchCriteria.getAuthor() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + searchCriteria.getAuthor().toLowerCase() + "%"));
        }
      /*  if (searchCriteria.getGenre() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("genres").as(String.class)), "%\"" + searchCriteria.getGenre().toLowerCase() + "\"%"));
        }*/
        if (searchCriteria.getDescription() != null) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchCriteria.getDescription().toLowerCase() + "%"));
        }
        if (searchCriteria.getYear() != null) {
            Expression<String> yearExpr = criteriaBuilder.function("TO_CHAR", String.class, root.get("publicationDate"), criteriaBuilder.literal("YYYY"));
            predicates.add(criteriaBuilder.like(yearExpr, "%" + searchCriteria.getYear() + "%"));
        } else {
            query.select(root);
        }

        query.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0]))).
                orderBy(criteriaBuilder.desc(root.get("publicationDate")));
        return entityManager.createQuery(query).getResultList();
    }
}
