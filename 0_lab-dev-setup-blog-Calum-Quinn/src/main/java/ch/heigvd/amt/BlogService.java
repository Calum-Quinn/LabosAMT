package ch.heigvd.amt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@ApplicationScoped
public class BlogService {

    @Inject
    EntityManager em;

    @Transactional
    public Author getOrCreateAuthor(String name) {
        Author author;
        List<Author> list = em.createQuery("SELECT a FROM Author a WHERE a.name = :name", Author.class)
                .setParameter("name", name)
                .getResultList();
        if (list.isEmpty()) {
            author = new Author(name);
            em.persist(author);
        } else {
            author = list.get(0);
        }
        return author;
    }


    @Transactional
    public void createPost(@Valid Post post) {
        em.persist(post);
    }

    @Transactional
    public List<Post> findAllPosts() {
        return em.createQuery("SELECT p FROM Post p", Post.class)
                .getResultList();
    }

    @Transactional
    public Post findPostById(Long id) {
        return em.find(Post.class, id);
    }

    @Transactional
    public Post findPostBySlug(String slug) {
        return em.createQuery("SELECT p FROM Post p WHERE p.slug = :slug", Post.class)
                .setParameter("slug", slug)
                .getSingleResult();
    }
}
