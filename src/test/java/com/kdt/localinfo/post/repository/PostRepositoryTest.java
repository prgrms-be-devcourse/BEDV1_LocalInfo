package com.kdt.localinfo.post.repository;

import com.kdt.localinfo.category.Category;
import com.kdt.localinfo.photo.Photo;
import com.kdt.localinfo.post.entity.Post;
import com.kdt.localinfo.user.entity.Region;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;
import java.util.ArrayList;

@Transactional
@SpringBootTest
class PostPersistenceTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("저장 테스트")
    void testSaveCategory() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        Category category = entityManager.find(Category.class, 1L);
        ArrayList<Photo> photos = new ArrayList<>();
        Photo photo1 = new Photo("url1");
        Photo photo2 = new Photo("url2");
        photos.add(photo1);
        photos.add(photo2);

        Post post = new Post(
                "first contents",
                new Region("neighborhood", "district", "city"),
                category,
                photos);
        post.setCategory(category);

        entityManager.persist(photo1);
        entityManager.persist(photo2);

        post.addPhoto(photo1);
        post.addPhoto(photo2);

        entityManager.persist(post);

        transaction.commit();
        entityManager.clear();

        Post foundPost = entityManager.find(Post.class, 1L);

        Assertions.assertThat(foundPost.getCategory().getName()).isEqualTo("우리동네질문");
        Assertions.assertThat(foundPost.getPhotos().size()).isEqualTo(2);

    }
}