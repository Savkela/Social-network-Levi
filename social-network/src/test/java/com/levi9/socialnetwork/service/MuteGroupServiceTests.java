package com.levi9.socialnetwork.service;

import com.levi9.socialnetwork.Exception.BadRequestException;
import com.levi9.socialnetwork.Exception.ResourceExistsException;
import com.levi9.socialnetwork.Exception.ResourceNotFoundException;
import com.levi9.socialnetwork.Model.MuteDuration;
import com.levi9.socialnetwork.Model.MuteGroup;
import com.levi9.socialnetwork.Model.MuteGroupId;
import com.levi9.socialnetwork.Repository.MuteGroupRepository;
import com.levi9.socialnetwork.Service.impl.MuteGroupServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@RunWith(MockitoJUnitRunner.class)
class MuteGroupServiceTests {
    private static final String NOT_FOUND_MESSAGE = "MuteGroup not found for these ids :: ";

    @Mock
    private MuteGroupRepository muteGroupRepository;

    @InjectMocks
    private MuteGroupServiceImpl muteGroupService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllComments() {
        shouldReturnMuteGroupList();
    }

    @Test
    void testGetMuteGroupByIds() throws ResourceNotFoundException {
        givenUserIdAndGroupIdShouldReturnMuteGroup();
        givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionGet();
    }

    @Test
    void testCreateMuteGroup() throws ResourceExistsException {
        givenMuteGroupShouldSaveAndReturnMuteGroup();
        givenMuteGroupShouldThrowResourceExistsException();
    }

    @Test
    void testMuteGroup() throws ResourceNotFoundException {
        givenUserIdGroupIdAndMuteDurationShouldSaveAndReturnMuteGroup();
        givenUserIdGroupIdAndMuteDurationShouldThrowResourceNotFoundException();
    }

    @Test
    void testUnmuteGroup() throws ResourceNotFoundException {
        givenUserIdAndGroupIdShouldChangeAndReturnMuteGroup();
        givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionUnmute();
    }

    @Test
    void testDeleteMuteGroup() throws ResourceNotFoundException {
        givenUserIdAndGroupIdShouldDeleteMuteGroup();
        givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionDelete();
    }

    @Test
    void testGetMuteDurationFromString() {
        givenStringShouldReturnMuteDuration();
        givenStringShouldThrowBadRequestException();
    }

    private void shouldReturnMuteGroupList() {
        List<MuteGroup> muteGroupList = List.of(
                MuteGroup.builder().userId(1L).groupId(1L).build(),
                MuteGroup.builder().userId(1L).groupId(2L).build()
        );

        given(muteGroupRepository.findAll())
                .willReturn(muteGroupList);

        List<MuteGroup> returnedMuteGroups = muteGroupService.getAllMuteGroups();
        assertThat(returnedMuteGroups).hasSize(2);
        assertThat(returnedMuteGroups.get(0).getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroups.get(0).getGroupId()).isEqualTo(1L);
        assertThat(returnedMuteGroups.get(1).getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroups.get(1).getGroupId()).isEqualTo(2L);
    }

    private void givenUserIdAndGroupIdShouldReturnMuteGroup() throws ResourceNotFoundException {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        MuteGroup muteGroup = MuteGroup.builder().userId(1L).groupId(1L).build();

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.ofNullable(muteGroup));

        MuteGroup returnedMuteGroup = muteGroupService.getMuteGroupByIds(1L, 1L);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);


    }

    private void givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionGet() {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> muteGroupService.getMuteGroupByIds(1L, 1L));
    }

    private void givenMuteGroupShouldSaveAndReturnMuteGroup() throws ResourceExistsException {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        MuteGroup muteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .build();

        given(muteGroupRepository.existsById(muteGroupId))
                .willReturn(false);
        given(muteGroupRepository.save(muteGroup))
                .willReturn(muteGroup);

        MuteGroup returnedMuteGroup = muteGroupService.createMuteGroup(muteGroup);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);

        muteGroup.setUserId(null);
        returnedMuteGroup = muteGroupService.createMuteGroup(muteGroup);
        assertThat(returnedMuteGroup.getUserId()).isNull();
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);

        muteGroup.setGroupId(null);
        returnedMuteGroup = muteGroupService.createMuteGroup(muteGroup);
        assertThat(returnedMuteGroup.getUserId()).isNull();
        assertThat(returnedMuteGroup.getGroupId()).isNull();

        muteGroup.setUserId(1L);
        returnedMuteGroup = muteGroupService.createMuteGroup(muteGroup);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isNull();
    }

    private void givenMuteGroupShouldThrowResourceExistsException() {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        MuteGroup muteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .build();

        given(muteGroupRepository.existsById(muteGroupId))
                .willReturn(true);
        assertThrows(ResourceExistsException.class,
                () -> muteGroupService.createMuteGroup(muteGroup));
    }

    void givenUserIdGroupIdAndMuteDurationShouldSaveAndReturnMuteGroup() throws ResourceNotFoundException {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        LocalDateTime endOfMute = LocalDateTime.now();
        LocalDateTime updatedEndOfMute = endOfMute.plus(Duration.ofHours(24));
        MuteGroup muteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .endOfMute(endOfMute)
                .isPermanent(false)
                .build();
        MuteGroup updatedMuteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .endOfMute(updatedEndOfMute)
                .isPermanent(false)
                .build();
        MuteDuration muteDuration = MuteDuration.HOURS_24;

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.of(muteGroup));
        given(muteGroupRepository.save(updatedMuteGroup))
                .willReturn(updatedMuteGroup);

        MuteGroup returnedMuteGroup = muteGroupService.muteGroup(1L, 1L, muteDuration);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getEndOfMute()).isEqualTo(updatedMuteGroup.getEndOfMute());
        assertThat(returnedMuteGroup.getIsPermanent()).isFalse();
    }

    void givenUserIdGroupIdAndMuteDurationShouldThrowResourceNotFoundException() {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        MuteDuration muteDuration = MuteDuration.HOURS_24;

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> muteGroupService.muteGroup(1L, 1L, muteDuration));
    }

    void givenUserIdAndGroupIdShouldChangeAndReturnMuteGroup() throws ResourceNotFoundException {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        LocalDateTime endOfMute = LocalDateTime.now().plus(Duration.ofDays(10));
        LocalDateTime updatedEndOfMute = LocalDateTime.now();
        MuteGroup muteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .endOfMute(endOfMute)
                .isPermanent(false)
                .build();
        MuteGroup updatedMuteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .endOfMute(updatedEndOfMute)
                .isPermanent(false)
                .build();

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.of(muteGroup));
        given(muteGroupRepository.save(updatedMuteGroup))
                .willReturn(updatedMuteGroup);

        MuteGroup returnedMuteGroup = muteGroupService.unmuteGroup(1L, 1L);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getEndOfMute()).isEqualTo(updatedMuteGroup.getEndOfMute());
        assertThat(returnedMuteGroup.getIsPermanent()).isFalse();

        muteGroup.setEndOfMute(LocalDateTime.now().minus(Duration.ofDays(10)));
        updatedMuteGroup.setEndOfMute(muteGroup.getEndOfMute());

        returnedMuteGroup = muteGroupService.unmuteGroup(1L, 1L);
        assertThat(returnedMuteGroup.getUserId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getGroupId()).isEqualTo(1L);
        assertThat(returnedMuteGroup.getEndOfMute()).isEqualTo(updatedMuteGroup.getEndOfMute());
        assertThat(returnedMuteGroup.getIsPermanent()).isFalse();
    }

    void givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionUnmute() {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> muteGroupService.unmuteGroup(1L, 1L));
    }

    void givenUserIdAndGroupIdShouldDeleteMuteGroup() throws ResourceNotFoundException {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);
        MuteGroup muteGroup = MuteGroup.builder()
                .userId(1L)
                .groupId(1L)
                .endOfMute(LocalDateTime.now())
                .isPermanent(false)
                .build();

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.of(muteGroup));
        willDoNothing().given(muteGroupRepository).delete(muteGroup);

        muteGroupService.deleteMuteGroup(1L, 1L);
    }

    void givenUserIdAndGroupIdShouldThrowResourceNotFoundExceptionDelete() {
        MuteGroupId muteGroupId = new MuteGroupId(1L, 1L);

        given(muteGroupRepository.findById(muteGroupId))
                .willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> muteGroupService.deleteMuteGroup(1L, 1L));
    }

    void givenStringShouldReturnMuteDuration() {
        MuteDuration returnedMuteDuration = muteGroupService.getMuteDurationFromString("HOURS_24");
        assertThat(returnedMuteDuration.isPermanent()).isFalse();
        assertThat(returnedMuteDuration.getDuration()).isEqualTo(Duration.ofHours(24));
    }

    void givenStringShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> muteGroupService.getMuteDurationFromString("HOURS_25"));
    }
}
