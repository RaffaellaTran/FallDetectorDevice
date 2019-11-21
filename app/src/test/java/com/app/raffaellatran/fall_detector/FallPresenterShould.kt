package com.app.raffaellatran.fall_detector

import com.app.raffaellatran.fall_detector.presenter.FallPresenter
import com.app.raffaellatran.fall_detector.ui.FallView
import com.app.raffaellatran.falldetectorlibrary.data.model.FallModel
import com.app.raffaellatran.falldetectorlibrary.data.service.FallRepository
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
class FallPresenterShould {

    @Mock
    lateinit var fallView: FallView
    @Mock
    lateinit var fallRepository: FallRepository
    @Mock
    lateinit var fallPresenter: FallPresenter

    private val fallDetectorModel =
        FallModel(
            fallDate = LocalDateTime.of(
                LocalDate.of(2019, 9, 22),
                LocalTime.of(12, 22, 32, 42)
            ), fallDuration = 34
        )
    private val fallList = arrayListOf(fallDetectorModel)
    private val fallEmptyList = arrayListOf<FallModel>()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        Schedulers.trampoline()
        fallPresenter.onCreate(fallView)
    }

    @After
    fun tearDown() {
        Schedulers.computation()
    }

    @Test
    fun `should show the list in the activity`() {
        `should give a successful fall detector service call`(fallList)
        fallPresenter.showTimeDurationFall()
        verify(fallPresenter).showTimeDurationFall()
    }

    @Test
    fun `should show a text instead of the list in the activity`() {
        `should give a successful fall detector service call`(fallEmptyList)
        fallPresenter.showTimeDurationFall()
        verify(fallPresenter).showTimeDurationFall()
    }

    @Test
    fun `should show an error in the activity`() {
        `should give a successful fall detector service call`(RuntimeException("test"))
        fallPresenter.showTimeDurationFall()
        verify(fallPresenter).showTimeDurationFall()
    }

    private fun `should give a successful fall detector service call`(result: List<FallModel>) {
        whenever(fallRepository.getAll()).thenReturn(Observable.just(result))
    }

    private fun `should give a successful fall detector service call`(exception: Throwable) =
        whenever(fallRepository.getAll()).thenReturn(Observable.error(exception))
}