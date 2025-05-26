// src/main/java/com/example/testaqua/service/AquariumService.java
package com.example.testaqua.service;

import com.example.testaqua.model.Aquarium;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AquariumService {
    private final List<Aquarium> store = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(1);

    /** 새 어항 등록 */
    public Aquarium register(String name, String owner) {
        Aquarium a = new Aquarium(seq.getAndIncrement(), name, owner);
        store.add(a);
        return a;
    }

    /** 전체 어항 조회 */
    public List<Aquarium> listAll() {
        // 수정 불가능한 뷰로 반환
        return Collections.unmodifiableList(store);
    }
}
