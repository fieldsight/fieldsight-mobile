# Contributing code to ODK Collect

This is a living document. If you see something that could be improved, edit this document and submit a pull request following the instructions below!

## Table of Contents
* [Submitting a pull request](#submitting-a-pull-request)
* [Making sure your pull request is accepted](#making-sure-your-pull-request-is-accepted)
* [The review process](#the-review-process)
* [Work in progress pull requests](#work-in-progress-pull-requests)
* [Triage issues](#triage-issues-)
* [Code style guidelines](#code-style-guidelines)
* [UI Components Style guidelines](#ui-components-style-guidelines)
* [Strings](#strings)
* [Code from external sources](#code-from-external-sources)

## Submitting a pull request
To contribute code to ODK Collect, you will need to open a [pull request](https://help.github.com/articles/about-pull-requests/) which will be reviewed by the community and then merged into the core project. Generally, a pull request is submitted when a unit of work is considered complete but it can sometimes be helpful to share ideas through a work in progress (WIP) pull request ([learn more](#work-in-progress-pull-requests)).

1. [Set up your development environment](https://github.com/opendatakit/collect#setting-up-your-development-environment).

1. To make sure you have the latest version of the code, set up this repository as [a remote for your fork](https://help.github.com/articles/configuring-a-remote-for-a-fork/) and then [sync your fork](https://help.github.com/articles/syncing-a-fork/).

1. Create a branch for the code you will be writing:

        git checkout -b NAME_OF_YOUR_BRANCH

1. If there is an [issue](https://github.com/opendatakit/collect/issues) corresponding to what you will work on, put `@opendatakit-bot claim` as a comment on issue to say you are claiming it. If this is your first time contributing to the repo, the bot will send you an invite. Once you accept this invite, the bot will assign you to the issue. If there is no issue yet, create one to provide background on the problem you are solving.

1. Once you've made incremental progress towards you goal, commit your changes with a meaningful commit message. Use [keywords for closing issues](https://help.github.com/articles/closing-issues-via-commit-messages/) to refer to issues and have them automatically close when your changes are merged.

        git commit -m "Do a thing. Fix #1."

1. Push changes to your fork at any time to make them publicly available:

        git push

1. Once you have completed your code changes, verify that you have followed the [style guidelines](https://github.com/opendatakit/collect/blob/master/CONTRIBUTING.md#style-guidelines).

1. When your changes are ready to be added to the core ODK Collect project, [open a pull request](https://help.github.com/articles/creating-a-pull-request/). Make sure to set the base fork to `opendatakit/collect`. Describe your changes in the comment, refer to any relevant issues using [keywords for closing issues](https://help.github.com/articles/closing-issues-via-commit-messages/) and tag any person you think might need to know about the changes.

1. Pull requests will be reviewed when committers have time. If you haven't received a review in 10 days, you may notify committers by putting `@opendatakit/collect` in a comment.

## Making sure your pull request is accepted
1. Confirm that your code compiles.

1. Verify the functionality. Ideally, include automated tests with each pull request. If that's not possible, describe in the pull request comment which cases you tried manually to confirm that your code works as expected. Attach a test form when appropriate. This form should only include questions which are useful for verifying your change.

1. Make sure that there is an issue that corresponds to the pull request and that it has been discussed by the community as necessary.

1. Keep your pull request focused on one narrow goal. This could mean addressing an issue with multiple, smaller pull requests. Small pull requests are easier to review and less likely to introduce bugs. If you would like to make stylistic changes to the code, create a separate pull request.

1. Run `./gradlew checkCode` and fix any errors.

1. Write clear code. Use descriptive names and create meaningful abstractions (methods, classes).

1. Document your reasoning. Your commit messages should make it clear why each change has been made.

1. Point out decisions you made and what alternatives you considered. If you're unsure about a particular approach, ask a question to make your own thinking clear and help the reviewer identify controversial parts of the proposed solution. For example: "here I returned a result object to represent the status after the transaction. I also considered throwing an exception in case of error but I didn't like that it made it unclear where an error happened. Which do you prefer and why?" This is particularly important for [work in progress pull requests](#work-in-progress-pull-requests).

1. If your pull request makes user-facing changes, we likely need to update documentation. [File an issue on the docs repo](https://github.com/opendatakit/docs/issues/new) describing the changes.

1. Follow the guidelines below.

## The review process
Bug fixes, pull requests corresponding to issues with a clearly stated goal and pull requests with clear tests and/or process for manual verification are given priority. Pull requests that are unclear or controversial may be tagged as `needs discussion` and/or may take longer to review.

We try to have at least two people review every pull request and we encourage everyone to participate in the review process to get familiar with the code base and help ensure higher quality. Reviewers should ask themselves some or all of the following questions:
- Was this change adequately discussed prior to implementation?
- Is the intended behavior clear under all conditions?
- What interesting cases should be verified?
- Is the behavior as intended in all cases?
- What other functionality could this PR affect? Does that functionality still work as intended?
- Was the change verified with several different devices and Android versions?
- Is the code easy to understand and to maintain?
- Is there sufficient detail to inform any changes to documentation?

When a pull request is first created, @opendatakit-bot tags it as `needs review` to indicate that code review is needed. Community members review the code and leave their comments, verifying that the changes included are relevant and properly address the issue. A maintainer does a thorough code review and when satisfied with the code, tags the pull request as `needs testing` to indicate the need for a manual [black-box testing](https://en.wikipedia.org/wiki/Black-box_testing) pass. A pull request may go back and forth between `needs testing` and `needs review` until the behavior is thoroughly verified. Once the behavior has been thoroughly verified, the pull request is tagged as `behavior verified`. A maintainer then merges the changes. Pull requests that need more complete reviews including review of approach and/or appropriateness are tagged with `reviews wanted`. Any community member is encouraged to participate in the review process!

Small fixes that target very particular bugs may occasionally be merged without a second review.

## Work in progress pull requests

Work in progress (WIP) pull requests are useful to illustrate a proposed direction and get early feedback before committing to a solution direction.

Work in progress pull requests:
- Should include `[WIP]` in front of the pull request title.
- Should specifically describe the proposed solution and feedback wanted.
- Will not be merged until you remove `[WIP]` from the title.

## Triage issues [![Open Source Helpers](https://www.codetriage.com/opendatakit/collect/badges/users.svg)](https://www.codetriage.com/opendatakit/collect)

In addition to contributing code, you can help to triage issues. This can include reproducing bug reports, or asking for vital information such as version numbers or reproduction instructions. If you would like to start triaging issues, one easy way to get started is to [subscribe to opendatakit/collect on CodeTriage](https://www.codetriage.com/opendatakit/collect).

## Code style guidelines
Follow the [Android style rules](http://source.android.com/source/code-style.html) and the [Google Java style guide](https://google.github.io/styleguide/javaguide.html).

## UI Components Style guidelines
Ensure that the added UI components are compatible with both light and dark themes.
Follow the below points to get the color for coloring the UI components like text and icons instead of directly using color values (eg. #000000 or R.color.colorName).

UI Component | Java | Xml _(layouts, drawables, vectors)_:
--- | --- | ---
text color | themeUtils.getPrimaryTextColor() | ?primaryTextColor
accent color | themeUtils.getAccentColor() | ?colorAccent
icon color | themeUtils.getIconColor() | ?iconColor

## Strings
Always use [string resources](https://developer.android.com/guide/topics/resources/string-resource.html) instead of literal strings. This ensures wording consistency across the project and also enables full translation of the app. Only make changes to the base `res/values/strings.xml` English file and not to the other language files. The translated files are generated from [Transifex](https://www.transifex.com/opendatakit/collect/) where translations can be submitted by the community. Names of software packages or other untranslatable strings should be placed in `res/values/untranslated.xml`.

## Dependency injection

As much as possible to facilitate simpler, more modular and more testable components you should follow the Dependency Inversion principle in Collect Code. An example tutorial on this concept can be found [here](https://www.seadowg.com/dip-lesson/).

Because many Android components (Activity and Fragment for instance) don't allow us control over their constructors Collect uses [Dagger](https://google.github.io/dagger/) to 'inject' dependencies. The configuration for Dagger can be found in [AppDepdendencyComponent](collect_app/src/main/java/org/odk/collect/android/injection/config/AppDependencyComponent.java). For any normal objects it is probably best to avoid Dagger and use normal Java constructors.

While it's important to read the Dagger [documentation](https://google.github.io/dagger/users-guide) we've provided some basic instructions on how to use Dagger within Collect below.

### Providing dependencies

To declare a new dependency that objects can inject add a `@Provider` method to the `AppDepedencyModule`:

```java
@Provider
public MyDependency providesMyDependency() {
    return MyDependency();
}
```

You can also have Dagger return the same instance every time (i.e. a Singleton) by annotating the method with `@Singleton` as well.

### Injecting dependencies into Activity/Fragment objects

To inject a dependency into the Activity you first need to make Dagger aware it's injecting into that Activity by adding an `inject` to the `AppDependencyComponent` (if it's not already there):

```java
void inject(MyActivity activity);
```

Then define a field with the `@Inject` annotation in your Activity:

```java
@Inject
MyDependency dependency;
```

To have Dagger inject the dependency you need to hook the injection into the Activity's `onCreate` (as this is the first part of the lifecycle we have access to):

```java
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DaggerUtils.getComponent(this).inject(this);
}
```

For Fragment objects you should hook into the `onAttach` lifecycle method instead:

```java
@Override
public void onAttach(Activity activity) {
    super.onAttach(activity);
    DaggerUtils.getComponent(activity).inject(this);
}
```

### Swapping out dependencies in tests

To swap out depdendencies in a Robolectric test you can override the module the Application object uses to inject objects using provided helpers:

```java
@Before
public void setup() {
    MyDependency mocked = mock(MyDependency.class);
    RobolectricHelpers.overrideAppDependencyModule(new AppDependencyModule() {
      @Override
      public MyDependency providesMyDependency() {
        return mocked;
      }
    });
}
```

## Code from external sources
ODK Collect is released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0). Please make sure that any code you include is an OSI-approved [permissive license](https://opensource.org/faq#permissive). **Please note that if no license is specified for a piece of code or if it has an incompatible license such as GPL, using it puts the project at legal risk**.

Sites with compatible licenses (including [StackOverflow](http://stackoverflow.com/)) will sometimes provide exactly the code snippet needed to solve a problem. You are encouraged to use such snippets in ODK Collect as long as you attribute them by including a direct link to the source. In addition to complying with the content license, this provides useful context for anyone reading the code.
