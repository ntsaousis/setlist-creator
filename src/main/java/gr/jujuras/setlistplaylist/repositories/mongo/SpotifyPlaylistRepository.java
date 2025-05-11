package gr.jujuras.setlistplaylist.repositories.mongo;

import gr.jujuras.setlistplaylist.model.documents.SpotifyPlaylist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpotifyPlaylistRepository extends MongoRepository<SpotifyPlaylist, String> {
    List<SpotifyPlaylist> findBySpotifyUserId(String userId);
}
