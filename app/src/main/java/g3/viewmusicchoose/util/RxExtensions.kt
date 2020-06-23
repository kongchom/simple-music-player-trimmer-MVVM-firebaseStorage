package g3.viewmusicchoose.util

import android.os.Handler
import android.os.Looper
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min

fun Disposable.addToCompositeDisposable(compositeDisposable: CompositeDisposable) {
  if(!compositeDisposable.isDisposed) {
    compositeDisposable.add(this)
  }
}

fun <T> Observable<T>.applyScheduler(): Observable<T> {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applyComputation(): Observable<T> {
  return this
    .subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applyScheduler(): Single<T> {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applyComputation(): Single<T> {
  return this
    .subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.toMainThread(): Observable<T> {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.toMainThread(): Single<T> {
  return this
    .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.toMainThread(): Completable {
  return this
    .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applyScheduler(): Completable {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applyComputation(): Completable {
  return this
    .subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Maybe<T>.applyScheduler(): Maybe<T> {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.applyScheduler(): Flowable<T> {
  return this
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
}

fun <T> ObservableEmitter<T>.checkDisposed(): ObservableEmitter<T>? {
  return if (this.isDisposed) {
    null
  } else {
    this
  }
}

fun <T> SingleEmitter<T>.checkDisposed(): SingleEmitter<T>? {
  return if (this.isDisposed) {
    null
  } else {
    this
  }
}

fun <T> CompletableEmitter.checkDisposed(): CompletableEmitter? {
  return if (this.isDisposed) {
    null
  } else {
    this
  }
}

fun <T> MaybeEmitter<T>.checkDisposed(): MaybeEmitter<T>? {
  return if (this.isDisposed) {
    null
  } else {
    this
  }
}

fun <T> Observable<T>.retryInterval(maxRetry: Int): Observable<T> {
  return this.retryWhen(RetryWithDelay(maxRetry))
}

fun <T> Observable<T>.retryInterval(maxRetry: Int, timeInterval: Long): Observable<T> {
  return this.retryWhen(RetryWithDelay(maxRetry, timeInterval))
}

private class RetryWithDelay : Function<Observable<Throwable>, Observable<*>> {

  private val interval: IntervalGenerator
  private val maxRetries: Int
  private var retryCount: Int

  constructor(maxRetries: Int) {
    this.maxRetries = maxRetries
    this.retryCount = 0
    interval = IntervalGenerator()
  }

  constructor(maxRetries: Int, timeInterval: Long) {
    this.maxRetries = maxRetries
    this.retryCount = 0
    this.interval = IntervalGenerator(timeInterval)
  }

  override fun apply(attempts: Observable<Throwable>): Observable<*> {
    return attempts.flatMap(Function<Throwable, Observable<*>> { throwable ->
      if (++retryCount < maxRetries) {
        return@Function Observable.timer(interval.next(), TimeUnit.MILLISECONDS)
      }
      Observable.error<Throwable>(throwable)
    })
  }
}

class IntervalGenerator {

  private val maxInterval: Long
  private var attempts: Int = 1

  constructor() : this(60000)

  constructor(maxInterval: Long) {
    this.maxInterval = maxInterval
  }

  fun next(): Long {
    val ret = generateInterval(attempts)
    if (ret < maxInterval) {
      ++attempts
    }
    return ret
  }

  fun reset() {
    this.attempts = 1
  }

  private fun generateInterval(k: Int): Long {
    val mean = pow(2, k) * 1000

    val ratio = mean / 3.0
    val gaussian = Random().nextGaussian()
    val revise = gaussian * ratio

    var ret = min(maxInterval, (mean + revise).toLong())
    if (ret < 0) ret = 0
    return ret
  }

  private fun pow(a: Int, b: Int): Int {
    var ret = a
    var k = b
    while (k-- > 1) {
      ret *= a
    }
    return ret
  }
}

// START: Safety fire error

fun <T> SingleEmitter<T>.onErrorSafety(e: Throwable) {
  try {
    Handler(Looper.getMainLooper()).post {
      if (!isDisposed) {
        tryOnError(e)
      }
    }
  } catch (e: Exception) {
  }
}

fun <T> ObservableEmitter<T>.onErrorSafety(e: Throwable) {
  try {
    Handler(Looper.getMainLooper()).post {
      if (!isDisposed) {
        tryOnError(e)
      }
    }
  } catch (e: Exception) {
  }
}

fun CompletableEmitter.onErrorSafety(e: Throwable) {
  try {
    Handler(Looper.getMainLooper()).post {
      if (!isDisposed) {
        tryOnError(e)
      }
    }
  } catch (e: Exception) {
  }
}

// END: Safety fire error