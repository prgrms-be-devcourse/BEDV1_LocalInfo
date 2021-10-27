package com.kdt.localinfo.post.repository;

import com.kdt.localinfo.post.Entity.Category;
import com.kdt.localinfo.post.Entity.CategoryName;
import com.kdt.localinfo.post.Entity.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

//@Transactional
@SpringBootTest
class PostPersistenceTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("단방향 저장 테스트")
    void testSave() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        Category category = entityManager.find(Category.class, 1L);
        Post post = new Post("first contents", category);
        post.setCategory(category);
        entityManager.persist(post);
        transaction.commit();

        entityManager.clear();

        Post foundPost = entityManager.find(Post.class, 1L);
        Assertions.assertThat(foundPost.getCategory().getName()).isEqualTo(CategoryName.동네맛집);
    }
}