# Image Downloading Library

A clean, efficient, and easy-to-understand image downloading library for Android following SOLID principles and design patterns.

## Features

- 🚀 **Simple API**: Easy-to-use fluent interface
- 💾 **Smart Caching**: Memory and disk caching with LRU eviction
- 🔄 **Async Operations**: Built with Kotlin Coroutines
- 🎯 **SOLID Principles**: Clean architecture and maintainable code
- 🛡️ **Error Handling**: Comprehensive error handling and recovery
- 📱 **Memory Efficient**: Automatic image resizing and memory management
- 🔧 **Configurable**: Flexible configuration options
- 🧪 **Testable**: Well-structured for unit testing

## Quick Start

### 1. Initialize the Library

```kotlin
// In your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with default configuration
        ImageLoader.initialize(this)
        
        // Or with custom configuration
        val config = ImageLoaderConfig.Builder(this)
            .memoryCacheSize(100 * 1024 * 1024) // 100MB
            .diskCacheSize(500 * 1024 * 1024)   // 500MB
            .timeout(60) // 60 seconds
            .enableLogging()
            .build()
        
        ImageLoader.initialize(this, config)
    }
}
```

### 2. Basic Usage

```kotlin
// Simple image loading
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView)

// With placeholder and error handling
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView)

// With resizing and scaling
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .resize(200, 200)
    .centerCrop()
    .into(imageView)
```

### 3. Advanced Usage with Callbacks

```kotlin
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView) { result ->
        when (result) {
            is ImageResult.Success -> {
                // Image loaded successfully
                println("Image loaded: ${result.bitmap.width}x${result.bitmap.height}")
            }
            is ImageResult.Error -> {
                // Handle error
                println("Error: ${result.exception.message}")
            }
            is ImageResult.Loading -> {
                // Handle loading state
                println("Loading...")
            }
        }
    }
```

## Architecture

The library follows SOLID principles and uses several design patterns:

### SOLID Principles

1. **Single Responsibility Principle (SRP)**: Each class has one reason to change
2. **Open/Closed Principle (OCP)**: Open for extension, closed for modification
3. **Liskov Substitution Principle (LSP)**: Derived classes are substitutable for base classes
4. **Interface Segregation Principle (ISP)**: No client depends on methods it doesn't use
5. **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions

### Design Patterns

- **Strategy Pattern**: Different caching and network implementations
- **Builder Pattern**: Fluent API for configuration and requests
- **Factory Pattern**: Creating configured instances
- **Facade Pattern**: Simple interface to complex subsystems
- **Observer Pattern**: Callback mechanism for async operations

### Package Structure

```
com.rahulyadav.imagedownlinglib/
├── core/                    # Core interfaces and models
├── cache/                   # Caching implementations
├── network/                 # Network layer
├── decoder/                 # Image decoding
├── config/                  # Configuration classes
├── factory/                 # Factory classes
├── utils/                   # Utility classes
└── ImageLoader.kt          # Main public API
```

## Configuration Options

### Memory Cache
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .memoryCacheSize(50 * 1024 * 1024) // 50MB
    .disableMemoryCache() // Disable memory cache
    .build()
```

### Disk Cache
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .diskCacheSize(200 * 1024 * 1024) // 200MB
    .disableDiskCache() // Disable disk cache
    .build()
```

### Network Configuration
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .timeout(30) // 30 seconds timeout
    .networkClient(customNetworkClient) // Custom network client
    .build()
```

### Logging
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .enableLogging() // Enable debug logging
    .build()
```

## Cache Management

```kotlin
// Get cache statistics
val stats = ImageLoader.getCacheStats()
println("Memory: ${stats.memoryCacheSize}/${stats.memoryCacheMaxSize}")
println("Disk: ${stats.diskCacheSize}/${stats.diskCacheMaxSize}")

// Clear all caches
ImageLoader.clearCache()
```

## Request Management

```kotlin
// Cancel specific request
ImageLoader.cancel("https://example.com/image.jpg")

// Cancel all pending requests
ImageLoader.cancelAll()
```

## Error Handling

The library provides comprehensive error handling with specific exception types:

```kotlin
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView) { result ->
        when (result) {
            is ImageResult.Error -> {
                when (result.exception) {
                    is ImageLoaderException.NetworkException -> {
                        // Handle network errors
                    }
                    is ImageLoaderException.DecodeException -> {
                        // Handle decoding errors
                    }
                    is ImageLoaderException.CacheException -> {
                        // Handle cache errors
                    }
                    is ImageLoaderException.OutOfMemoryException -> {
                        // Handle memory issues
                    }
                }
            }
        }
    }
```

## Performance Tips

1. **Use appropriate cache sizes** based on your app's memory constraints
2. **Resize images** to match your UI requirements
3. **Use centerCrop()** for consistent aspect ratios
4. **Cancel requests** when views are no longer visible
5. **Enable logging** only in debug builds

## Testing

The library is designed to be easily testable:

```kotlin
// Mock dependencies for testing
val mockCache = mockk<ImageCache>()
val mockNetworkClient = mockk<NetworkClient>()
val mockDecoder = mockk<ImageDecoder>()

val config = ImageLoaderConfig.Builder(context)
    .memoryCache(mockCache)
    .networkClient(mockNetworkClient)
    .imageDecoder(mockDecoder)
    .build()
```

## Dependencies

- **Kotlin Coroutines**: For async operations
- **OkHttp**: For network requests
- **AndroidX Core**: For context and lifecycle management

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 1.8+
- **Java**: 11+

## License

This library is part of the AnalyticsLib project and follows the same licensing terms.

## Contributing

When contributing to this library:

1. Follow SOLID principles
2. Write unit tests for new features
3. Update documentation
4. Ensure backward compatibility
5. Follow the existing code style

## Interview Tips

When explaining this library in interviews, focus on:

1. **SOLID Principles**: How each principle is applied
2. **Design Patterns**: Which patterns are used and why
3. **Architecture**: Clean separation of concerns
4. **Performance**: Caching strategies and memory management
5. **Error Handling**: Comprehensive error scenarios
6. **Testability**: How the design enables easy testing
7. **Extensibility**: How to add new features without breaking existing code

This library demonstrates professional Android development practices and is perfect for showcasing your understanding of clean architecture and design patterns.
