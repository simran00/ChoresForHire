Original App Design Project - README
===

# Chores For Hire

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Connects people who need chores done with people who are willing to do the chores and get paid.

### App Evaluation
- **Category:** Matching App
- **Mobile:** This app would be primarily developed for mobile.
- **Story:** Serves as a platform for people to post tasks that they wish to be done. Others can choose to do that task and get paid the specified amount.
- **Market:** Anyone can use this. But some tasks may have minimum age requirements.
- **Habit:** This app would be used whenever someone wants to earn a bit of money or wants something to be done.
- **Scope:** This app would initially be simply a platform where users can post and accept tasks. Later, it could evolve to have different community groups where each community group specializes in a specific field (e.g. mowing lawns). This would make it easier for users with specific needs to find people.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create an account and log in.
* User can access all the available tasks.
* User can post tasks.
* User can accept tasks.
* Users can view where task must be accomplished (can see location).

**Optional Nice-to-have Stories**

* Users can chat with each other.
* Users can join task-specific groups.

### 2. Screen Archetypes

* Login Screen
   * User can log in.
* Registration Screen
   * User can create a new account.
* Stream
    * User can scroll through all the available tasks.
    * Users can view the tasks in a map view (pins on where the task must be accomplished). 
* Creation
    * User can create a new task.
* Profile
    * User can see their ratings/reviews.
    * Users include their contact information.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed
* Post an Activity
* Search activities

**Flow Navigation** (Screen to Screen)

* Login Screen
    * Home
* Registration Screen
    * Home
* Home Feed Screen
    * Expanded detail page of task
    * Map view
* Creation Screen
    * Home
* Search Screen
   * Expanded detail page of task
* Tasks Screen
    * List of tasks to be done

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>
![](https://i.imgur.com/YXiNMBy.jpg)

### Models


Map
| Property        | Type        |  Description                               |
| --------        | --------    | --------                                   |
| objectID        | String      | unique id for the user post (default field)|
| currLocation    | JSON Object | location of user                           |
| destLocation    | JSON Object | location of tasks                          |
| createdAt       | DateTime    | date when post is created (default field)  |

### Networking
- Home Feed Screen
    - (Read/GET) Query all active tasks near location
    ```
    private void showClosestUser(final GoogleMap googleMap){
    ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.whereNear("Location", getCurrentUserLocation());
    // setting the limit of near users to find to 2, you'll have in the nearUsers list only two users: the current user and the closest user from the current
    query.setLimit(2);
    query.findInBackground(new FindCallback<ParseUser>() {
        @Override  public void done(List<ParseUser> nearUsers, ParseException e) {
            if (e == null) {
                // avoiding null pointer
                ParseUser closestUser = ParseUser.getCurrentUser();
                // set the closestUser to the one that isn't the current user
                for(int i = 0; i < nearUsers.size(); i++) {
                    if(!nearUsers.get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        closestUser = nearUsers.get(i);
                    }
                }
                // finding and displaying the distance between the current user and the closest user to him, using method implemented in Step 4
                double distance = getCurrentUserLocation().distanceInKilometersTo(closestUser.getParseGeoPoint("Location"));
                alertDisplayer("We found the closest user from you!", "It's " + closestUser.getUsername() + ". \n You are " + Math.round (distance * 100.0) / 100.0  + " km from this user.");
                // showing current user in map, using the method implemented in Step 5
                showCurrentUserInMap(mMap);
                // creating a marker in the map showing the closest user to the current user location
                LatLng closestUserLocation = new LatLng(closestUser.getParseGeoPoint("Location").getLatitude(), closestUser.getParseGeoPoint("Location").getLongitude());
                googleMap.addMarker(new MarkerOptions().position(closestUserLocation).title(closestUser.getUsername()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                // zoom the map to the currentUserLocation
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closestUserLocation, 3));
            } else {
                Log.d("store", "Error: " + e.getMessage());
            }
        }
    });
    ParseQuery.clearAllCachedResults();
    }
    ```
- Create Post Screen
    - (Create/POST) Create a new post object
- Tasks Screen
    - (Read/GET) Query all accepted tasks
    - (Delete) Delete posts

- [OPTIONAL: List endpoints if using existing API such as Yelp]
- Google Maps API:
