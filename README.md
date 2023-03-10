# RedditApp

This app is a simple RSS-feed app which works with Reddit API. It can show posts by choosing a subreddit. The app consists of several activities: feed, comments, account and post in WebView. It allows you to view feed, log in to reddit account and share a comment. **This is a learning project.**

Topics covered:

1. Java + Kotlin
2. Android SDK and Android Studio
3. XML-based interfaces
4. Retrofit + parsing JSON and XML
5. UniversalImageLoader
6. RecyclerView
8. Toolbars, Navigation Bars, Menus
9. Activity-intent
10. CardView
11. WebView
12. Android Material Design


This app is an implementation of [CodingWithMitch course](https://github.com/mitchtabian/Reddit-RSS-App) with some adjustments:

1. `ListView` and `CustomListAdapter` in MainActivity updated to `RecyclerView` and `PostAdapter`

1. `ListView` and `CommentsListAdapter` in CommentsActivity updated to `RecyclerView` and `CommentAdapter`

1. All the `RelativeLayouts` switched to `ConstraintLayouts`

1. The app shows **"funny"** subreddit instead of empty screen and error on app launch

1. Fixed the issue with first comment always error

1. Formatted date and time of posts and comments to look pretty and be shown in local time

1. Added a **login icon** instead of text button in options menu

1. Other minor design improvments



