package com.ifortex.bookservice.repository.impl;

import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final EntityManagerFactory entityManagerFactory;

    public MemberRepositoryImpl(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Member findMemberByRomance() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Member member = new Member();
        try {
            Query query = entityManager.createNativeQuery("select m.id, m.name, m.membership_date from members m\n" +
                    "join member_books mb on m.id = mb.member_id\n" +
                    "join books b on mb.book_id = b.id\n" +
                    "where 'Romance' = ANY (b.genre)\n" +
                    "order by m.membership_date asc, b.publication_date desc\n" +
                    "limit 1", Member.class);
            member = (Member) query.getSingleResult();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        return member;
    }

    @Override
    public List<Member> findMembers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Query query = null;
        try {
            query = entityManager.createNativeQuery("select m.id, m.name, membership_date from members m\n" +
                    "       left join member_books mb on m.id = mb.member_id\n" +
                    "       where membership_date between '2023-01-01 00:00:00' and '2023-12-31 23:59:59'\n" +
                    "       and mb.member_id is null;");
            entityManager.getTransaction().commit();
        }catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
        return query.getResultList();
    }
}
