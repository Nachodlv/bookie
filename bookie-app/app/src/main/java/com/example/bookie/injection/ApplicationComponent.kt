package com.example.bookie.injection

import com.example.bookie.api.client.UserClient
import com.example.bookie.view_models.UserViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UserClient::class])
interface ApplicationComponent {
    // This tells Dagger that UserViewModel requests injection so the graph needs to
    // satisfy all the dependencies of the fields that UserViewModel is requesting.
    fun inject(activity: UserViewModel)
}
