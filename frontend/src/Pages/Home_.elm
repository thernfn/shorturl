module Pages.Home_ exposing (page, Model, Msg)

import Html exposing (Html)
import Html.Attributes as Attr
import View exposing (View)
import Html.Events as Events
import Effect exposing (Effect)
import Api.PostUrl
import Http
import Page exposing (Page)
import Shared
import Route exposing (Route)

page : Shared.Model -> Route () -> Page Model Msg
page _ _ =
    Page.new
        { init = init
        , update = update
        , subscriptions = subscriptions
        , view = view
        }
        
-- INIT

type alias Model =
    { url : String
    , slug : String
    , isSubmittingForm : Bool
    }

init : () -> ( Model, Effect Msg )
init () =
    ( { url = ""
      , slug = ""
      , isSubmittingForm = False
      }
    , Effect.none
    )


-- Update

type Msg
    = UserUpdatedInput Field String
    | UserSubmittedForm
    | PostUrlApiResponded (Result Http.Error Api.PostUrl.Data)

type Field = URL
    
update : Msg -> Model -> ( Model, Effect Msg )
update msg model =
    case msg of
        UserUpdatedInput URL value ->
            ( { model | url = value }
            , Effect.none
            )

        UserSubmittedForm ->
            ( { model | isSubmittingForm = True}
            , Api.PostUrl.post
                { onResponse = PostUrlApiResponded
                , url = model.url
                }
            )

        PostUrlApiResponded (Ok { slug }) ->
            ( { model | isSubmittingForm = False,  slug = slug}
            , Effect.none)

        PostUrlApiResponded (Err httpError) ->
            ( { model | isSubmittingForm = False }
            , Effect.none
            )
                
-- Subscription

subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none
        
-- View

view : Model -> View Msg
view model =
    { title = "Short It"
    , body =
          [ viewPage model
          ]
    }

viewPage : Model -> Html Msg
viewPage model =
    Html.div [ Attr.class "bg-pink-100 grid place-items-center h-screen"]
        [ viewRedirectOrInput model ]

viewRedirectOrInput : Model -> Html Msg
viewRedirectOrInput model =
    if model.slug == "" then
        Html.div []
            [ Html.input
                  [ Attr.class "form-control border border-solid"
                  , Attr.placeholder "Enter URL"
                  , Attr.value model.url
                  , Events.onInput (UserUpdatedInput URL)
                  ] []
            , Html.button [ Attr.class "border-2 rounded px-4 uppercase"
                          , Attr.disabled model.isSubmittingForm
                          , Events.onClick UserSubmittedForm
                          ]
                [ Html.text "Shorten Url"]
            ]
    else
        let
            redirectUrl : String
            redirectUrl = "http://localhost:3001/get/" ++ model.slug
        in
            Html.div []
                [ Html.a [ Attr.href redirectUrl ]
                      [ Html.text redirectUrl ]
                ]
