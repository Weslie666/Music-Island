package com.musicisland.service.impl;

import com.musicisland.entity.Song;
import com.musicisland.entity.UserBehavior;
import com.musicisland.mapper.SongMapper;
import com.musicisland.mapper.UserBehaviorMapper;
import com.musicisland.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private UserBehaviorMapper behaviorMapper;

    @Override
    public List<Song> getPopularRecommendations(int limit) {
        return songMapper.findPopular(0, limit);
    }

    @Override
    public List<Song> getPersonalRecommendations(Long userId, int limit) {
        List<UserBehavior> myBehaviors = behaviorMapper.findByUserId(userId);

        // Cold start: fallback to popular if fewer than 3 behaviors
        if (myBehaviors.size() < 3) {
            return getPopularRecommendations(limit);
        }

        // Only exclude songs the user has liked (not just played)
        Set<Long> myLikedIds = myBehaviors.stream()
                .filter(ub -> ub.getLikeFlag() == 1)
                .map(UserBehavior::getSongId).collect(Collectors.toSet());

        // Build user-item score vectors: userId -> {songId -> score}
        List<UserBehavior> allBehaviors = behaviorMapper.findAll();
        Map<Long, Map<Long, Double>> userVectors = new HashMap<>();
        for (UserBehavior ub : allBehaviors) {
            userVectors.computeIfAbsent(ub.getUserId(), k -> new HashMap<>())
                    .put(ub.getSongId(), computeScore(ub));
        }

        if (!userVectors.containsKey(userId)) return getPopularRecommendations(limit);

        Map<Long, Double> myVector = userVectors.get(userId);

        // Compute cosine similarity with all other users
        Map<Long, Double> similarities = new HashMap<>();
        for (Map.Entry<Long, Map<Long, Double>> entry : userVectors.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) continue;
            double sim = cosineSimilarity(myVector, entry.getValue());
            if (sim > 0) similarities.put(otherUserId, sim);
        }

        // Select top-K similar users (K=10)
        List<Long> topKUsers = similarities.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Aggregate song scores from similar users
        Map<Long, Double> songScores = new HashMap<>();
        for (Long similarUserId : topKUsers) {
            double sim = similarities.get(similarUserId);
            Map<Long, Double> otherVector = userVectors.get(similarUserId);
            for (Map.Entry<Long, Double> e : otherVector.entrySet()) {
                Long songId = e.getKey();
                if (!myLikedIds.contains(songId)) {
                    songScores.merge(songId, sim * e.getValue(), Double::sum);
                }
            }
        }

        // Sort and take top N, fetch songs
        List<Long> recommendedIds = songScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedIds.isEmpty()) return getPopularRecommendations(limit);

        return recommendedIds.stream()
                .map(songMapper::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> getSimilarSongs(Long songId, int limit) {
        Song source = songMapper.findById(songId);
        if (source == null) return Collections.emptyList();

        // Get all songs except source
        List<Song> allSongs = songMapper.findPopular(0, 200);
        allSongs.removeIf(s -> s.getId().equals(songId));

        // Score by genre + artist match
        Map<Song, Double> scored = new HashMap<>();
        for (Song s : allSongs) {
            double score = 0;
            if (source.getGenre() != null && source.getGenre().equals(s.getGenre())) {
                score += 0.6;
            }
            if (source.getArtist() != null && source.getArtist().equals(s.getArtist())) {
                score += 0.4;
            }
            // Genre overlap bonus (partial match)
            if (score == 0 && source.getGenre() != null && s.getGenre() != null) {
                String[] g1 = source.getGenre().split("\\s+");
                String[] g2 = s.getGenre().split("\\s+");
                for (String a : g1)
                    for (String b : g2)
                        if (a.equals(b)) { score = 0.3; break; }
            }
            if (score > 0) {
                long pc = s.getPlayCount() != null ? s.getPlayCount() : 0;
                score += Math.log(pc + 2) * 0.1; // popularity tiebreaker
                scored.put(s, score);
            }
        }

        return scored.entrySet().stream()
                .sorted(Map.Entry.<Song, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double computeScore(UserBehavior ub) {
        return ub.getPlayCount() * 0.3 + ub.getLikeFlag() * 0.7;
    }

    private double cosineSimilarity(Map<Long, Double> v1, Map<Long, Double> v2) {
        if (v1.isEmpty() || v2.isEmpty()) return 0;

        double dot = 0, norm1 = 0, norm2 = 0;

        for (Map.Entry<Long, Double> e : v1.entrySet()) {
            Double val2 = v2.get(e.getKey());
            if (val2 != null) dot += e.getValue() * val2;
            norm1 += e.getValue() * e.getValue();
        }

        for (double v : v2.values()) norm2 += v * v;

        if (norm1 == 0 || norm2 == 0) return 0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
