module Api.PostUrl exposing (Data, post)

import Effect exposing (Effect)
import Http
import Json.Decode
import Json.Encode

-- The data we expect if the sign in attempt was successful.
type alias Data =
    { slug : String
    , url : String
    }

-- How to crate a `Data` value from JSON
decoder : Json.Decode.Decoder Data
decoder =
    Json.Decode.map2 Data
        slugDecoder
        urlDecoder

slugDecoder : Json.Decode.Decoder String
slugDecoder =
    Json.Decode.field "slug" Json.Decode.string

urlDecoder : Json.Decode.Decoder String
urlDecoder =
    Json.Decode.field "url" Json.Decode.string

-- Sends a Post request to our `/post/url` endpoint, which
-- returns {"slug": "random", "url": "http..."} after successful
-- shortening.

post :
    { onResponse : Result Http.Error Data -> msg
    , url : String
    }
    -> Effect msg
post options =
    let
        body : Json.Encode.Value
        body =
            Json.Encode.object
                [ ( "url", Json.Encode.string options.url ) ]

        cmd : Cmd msg
        cmd =
            Http.post
                { url = "http://localhost:3001/post/url"
                , body = Http.jsonBody body
                , expect = Http.expectJson options.onResponse decoder
                }
    in
        Effect.sendCmd cmd
            
                    
