package org.jgy.chatserver.member.repository;

import org.jgy.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmail(String email);
}