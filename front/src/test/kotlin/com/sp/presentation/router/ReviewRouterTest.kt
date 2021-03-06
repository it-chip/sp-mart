package com.sp.presentation.router

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.SimpleType
import com.ninjasquad.springmockk.MockkBean
import com.sp.application.ReviewCommandService
import com.sp.application.ReviewQueryService
import com.sp.application.ReviewSummary
import com.sp.presentation.FrontApiTestSupportFilterFunction
import com.sp.presentation.MemberInfoConstant
import com.sp.presentation.MemberInfoFilter
import com.sp.presentation.handler.ReviewHandler
import com.sp.presentation.model.PageInfo
import com.sp.presentation.request.ReviewRegisterRequest
import io.mockk.coEvery
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.PageImpl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime

/**
 * @author Jaedoo Lee
 */
@WebFluxTest
@ExtendWith(RestDocumentationExtension::class)
@ContextConfiguration(classes = [ReviewRouter::class, ReviewHandler::class, MemberInfoFilter::class])
internal class ReviewRouterTest(private val context: ApplicationContext) {

    private lateinit var webTestClient: WebTestClient

    private val TAG = "REVIEW"

    @MockkBean
    private lateinit var reviewCommandService: ReviewCommandService

    @MockkBean
    private  lateinit var reviewQueryService: ReviewQueryService

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        webTestClient = WebTestClient.bindToApplicationContext(context)
            .configureClient()
            .baseUrl("http://localhost:8081")
            .filter(WebTestClientRestDocumentation.documentationConfiguration(restDocumentation))
            .filter(FrontApiTestSupportFilterFunction())
            .build()
    }

    @Test
    fun `?????? ??????`() {
        val storeNo = 1L
        val request = ReviewRegisterRequest(
            title = "?????????",
            content = "?????? ???????????? ????????? ?????? ??? ?????? ????????? ???????????? ????????? ??? ????????? ?????? ????????? ?????? ??? ??? ??????..",
            imageUrl = null,
            accessible = true,
            reliable = true
        )

        coEvery { reviewCommandService.registerReview(any()) } returns 1L

        webTestClient.post()
            .uri("/reviews/{storeNo}", storeNo)
            .header("Version", "1.0")
            .header(MemberInfoConstant.ACCESS_TOKEN_HEADER, MemberInfoConstant.TEST_ACCESS_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().valueEquals(HttpHeaders.LOCATION, storeNo.toString())
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    "review-register",
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .description("????????? ?????? ??????")
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Version")
                                    .description("??????"),
                                ResourceDocumentation.headerWithName(MemberInfoConstant.ACCESS_TOKEN_HEADER)
                                    .description("AccessToken")
                            )
                            .requestFields(
                                PayloadDocumentation.fieldWithPath("title")
                                    .description("??????")
                                    .type(JsonFieldType.STRING),
                                PayloadDocumentation.fieldWithPath("content")
                                    .description("??????")
                                    .type(JsonFieldType.STRING),
                                PayloadDocumentation.fieldWithPath("imageUrl")
                                    .description("????????? ??????")
                                    .type(JsonFieldType.STRING)
                                    .optional(),
                                PayloadDocumentation.fieldWithPath("accessible")
                                    .description("?????? ????????? ?????????")
                                    .type(JsonFieldType.BOOLEAN),
                                PayloadDocumentation.fieldWithPath("reliable")
                                    .description("?????? ?????? ?????????")
                                    .type(JsonFieldType.BOOLEAN)
                            ).build()
                    )
                )
            )
    }

    @Test
    fun `??????????????? ?????? ??????`() {
        val pageInfo = PageInfo(1, 10)

        val response = PageImpl((1L..10L).map {
            ReviewSummary(
                reviewNo = it,
                title = "??????",
                content = "??? ?????? ????????? ??? ???????????? ????????? ???????????? ?????? ?????? ????????? ?????? ??? ???????????? ?????? ??? ??? ?????? ??? ?????? ????????????",
                imageUrl = "http://image.url",
                memberNo = it,
                storeNo = it,
                accessible = true,
                reliable = true,
                registerYmdt = LocalDateTime.now(),
                updateYmdt = null
            )
        }).toList()

        coEvery { reviewQueryService.getReviewsByMember(any(), any()) } returns response

        webTestClient.get()
            .uri("/reviews?page=${pageInfo.pageNumber}&pageSize=${pageInfo.pageSize}")
            .header("Version", "1.0")
            .header(MemberInfoConstant.ACCESS_TOKEN_HEADER, MemberInfoConstant.TEST_ACCESS_TOKEN)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    "mypage-reviews",
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .description("??????????????? ?????? ??????")
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Version")
                                    .description("??????"),
                                ResourceDocumentation.headerWithName(MemberInfoConstant.ACCESS_TOKEN_HEADER)
                                    .description("AccessToken")
                            ).requestParameters(
                                ResourceDocumentation.parameterWithName("page")
                                    .description("?????????")
                                    .type(SimpleType.NUMBER),
                                ResourceDocumentation.parameterWithName("pageSize")
                                    .description("????????? ??????")
                                    .type(SimpleType.NUMBER)
                            ).responseFields(*toDescriptors("[].")).build()
                    )
                )
            )
    }

    @Test
    fun `????????? ?????? ??????`() {
        val storeNo = 1L
        val pageInfo = PageInfo(1, 10)

        val response = PageImpl((1L..10L).map {
            ReviewSummary(
                reviewNo = it,
                title = "??????",
                content = "??? ?????? ????????? ??? ???????????? ????????? ???????????? ?????? ?????? ????????? ?????? ??? ???????????? ?????? ??? ??? ?????? ??? ?????? ????????????",
                imageUrl = "http://image.url",
                memberNo = it,
                storeNo = it,
                accessible = true,
                reliable = true,
                registerYmdt = LocalDateTime.now(),
                updateYmdt = null
            )
        }).toList()

        coEvery { reviewQueryService.getReviewsByStore(any(), any()) } returns response

        webTestClient.get()
            .uri("/reviews/{storeNo}?page=${pageInfo.pageNumber}&pageSize=${pageInfo.pageSize}", storeNo)
            .header("Version", "1.0")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().consumeWith(
                WebTestClientRestDocumentation.document(
                    "store-reviews",
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag(TAG)
                            .description("????????? ?????? ??????")
                            .requestHeaders(
                                ResourceDocumentation.headerWithName("Version")
                                    .description("??????")
                            ).requestParameters(
                                ResourceDocumentation.parameterWithName("page")
                                    .description("?????????")
                                    .type(SimpleType.NUMBER),
                                ResourceDocumentation.parameterWithName("pageSize")
                                    .description("????????? ??????")
                                    .type(SimpleType.NUMBER)
                            ).responseFields(*toDescriptors("[].")).build()
                    )
                )
            )
    }
}
