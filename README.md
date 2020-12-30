## BlocksViewer
This simple app displays the latest block list from [EOSIO Testnet](https://eos.io/build-on-eosio/eosio-testnet/) using [Chain API](https://developers.eos.io/manuals/eos/latest/nodeos/plugins/chain_api_plugin/api-reference/index), each list cell will lead to a block detail screen which contains the summary of the block as well as the raw json content.

The list is sorted in reverse chronological order, the most recent block will appear at the top, with a swipe to refresh option. In addition, you can scroll to the bottom, the list will lazily load the next page of blocks, the default page size is 20.

#### Features
* MVVM
* [Dagger 2](https://dagger.dev/dev-guide) for Inversion of control
* [Kotlin Flow](https://developer.android.com/kotlin/flow)
* Inifinte scrolling using [Paging 3 Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

#### Prerequisites
* Java JDK 1.8+ (1.8 source compatibility is targeted)
* Android Studio 4.1.1
* Android API 21

#### Run test
`./gradlew :app:testDebugUnitTest`

#### Screen Record

#### Reference
[Android Paging Sample](https://github.com/googlecodelabs/android-paging)

