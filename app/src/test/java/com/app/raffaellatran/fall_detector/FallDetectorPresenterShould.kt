package com.app.raffaellatran.fall_detector

import com.app.raffaellatran.fall_detector.presenter.FallDetectorPresenter
import com.app.raffaellatran.fall_detector.ui.FallDetectorView
import com.app.raffaellatran.falldetectorlibrary.data.model.FallDetectorModel
import com.app.raffaellatran.falldetectorlibrary.data.service.FallDetectorRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RunWith(MockitoJUnitRunner.Silent::class)
class FallDetectorPresenterShould {

    @Mock
    lateinit var fallDetectorView: FallDetectorView
    @Mock
    lateinit var fallDetectorRepository: FallDetectorRepository
    @Mock
    lateinit var fallDetectorPresenter: FallDetectorPresenter

    private val fallDetectorModel =
        FallDetectorModel(
            fallDate = LocalDateTime.of(
                LocalDate.of(2019, 9, 22),
                LocalTime.of(12, 22, 32, 42)
            ), fallDuration = 34
        )
    private val fallList = arrayListOf(fallDetectorModel)
    private val fallEmptyList = arrayListOf<FallDetectorModel>()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        Schedulers.trampoline()
        fallDetectorPresenter.onCreate(fallDetectorView)
    }

    @After
    fun tearDown() {
        Schedulers.computation()
    }

    @Test
    fun `should show the list in the activity`() {
        `should give a successful fall detector service call`(fallList)
        fallDetectorPresenter.showTimeDurationFall()
        verify(fallDetectorPresenter).showTimeDurationFall()
    }

    @Test
    fun `should show a text instead of the list in the activity`() {
        `should give a successful fall detector service call`(fallEmptyList)
        fallDetectorPresenter.showTimeDurationFall()
        verify(fallDetectorPresenter).showTimeDurationFall()
    }

    @Test
    fun `should show an error in the activity`() {
        `should give a successful fall detector service call`(RuntimeException("test"))
        fallDetectorPresenter.showTimeDurationFall()
        verify(fallDetectorPresenter).showTimeDurationFall()
    }

    private fun `should give a successful fall detector service call`(result: List<FallDetectorModel>) {
        whenever(fallDetectorRepository.getAll()).thenReturn(Observable.just(result))
    }

    private fun `should give a successful fall detector service call`(exception: Throwable) =
        whenever(fallDetectorRepository.getAll()).thenReturn(Observable.error(exception))
}