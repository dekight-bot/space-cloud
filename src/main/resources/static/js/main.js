/*
	Hielo by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
*/

var settings = {

	banner: {

		// Indicators (= the clickable dots at the bottom).
			indicators: true,

		// Transition speed (in ms)
		// For timing purposes only. It *must* match the transition speed of "#banner > article".
			speed: 1500,

		// Transition delay (in ms)
			delay: 5000,

		// Parallax intensity (between 0 and 1; higher = more intense, lower = less intense; 0 = off)
			parallax: 0.25

	}

};

(function($) {

	skel.breakpoints({
		xlarge:	'(max-width: 1680px)',
		large:	'(max-width: 1280px)',
		medium:	'(max-width: 980px)',
		small:	'(max-width: 736px)',
		xsmall:	'(max-width: 480px)'
	});

	/**
	 * Applies parallax scrolling to an element's background image.
	 * @return {jQuery} jQuery object.
	 */
	$.fn._parallax = (skel.vars.browser == 'ie' || skel.vars.mobile) ? function() { return $(this) } : function(intensity) {

		var	$window = $(window),
			$this = $(this);

		if (this.length == 0 || intensity === 0)
			return $this;

		if (this.length > 1) {

			for (var i=0; i < this.length; i++)
				$(this[i])._parallax(intensity);

			return $this;

		}

		if (!intensity)
			intensity = 0.25;

		$this.each(function() {

			var $t = $(this),
				on, off;

			on = function() {

				$t.css('background-position', 'center 100%, center 100%, center 0px');

				$window
					.on('scroll._parallax', function() {

						var pos = parseInt($window.scrollTop()) - parseInt($t.position().top);

						$t.css('background-position', 'center ' + (pos * (-1 * intensity)) + 'px');

					});

			};

			off = function() {

				$t
					.css('background-position', '');

				$window
					.off('scroll._parallax');

			};

			skel.on('change', function() {

				if (skel.breakpoint('medium').active)
					(off)();
				else
					(on)();

			});

		});

		$window
			.off('load._parallax resize._parallax')
			.on('load._parallax resize._parallax', function() {
				$window.trigger('scroll');
			});

		return $(this);

	};

	/**
	 * Custom banner slider for Slate.
	 * @return {jQuery} jQuery object.
	 */
	$.fn._slider = function(options) {

		var	$window = $(window),
			$this = $(this);

		if (this.length == 0)
			return $this;

		if (this.length > 1) {

			for (var i=0; i < this.length; i++)
				$(this[i])._slider(options);

			return $this;

		}

		// Vars.
			var	current = 0, pos = 0, lastPos = 0,
				slides = [], indicators = [],
				$indicators,
				$slides = $this.children('article'),
				intervalId,
				isLocked = false,
				i = 0;

		// Turn off indicators if we only have one slide.
			if ($slides.length == 1)
				options.indicators = false;

		// Functions.
			$this._switchTo = function(x, stop) {

				if (isLocked || pos == x)
					return;

				isLocked = true;

				if (stop)
					window.clearInterval(intervalId);

				// Update positions.
					lastPos = pos;
					pos = x;

				// Hide last slide.
					slides[lastPos].removeClass('top');

					if (options.indicators)
						indicators[lastPos].removeClass('visible');

				// Show new slide.
					slides[pos].addClass('visible').addClass('top');

					if (options.indicators)
						indicators[pos].addClass('visible');

				// Finish hiding last slide after a short delay.
					window.setTimeout(function() {

						slides[lastPos].addClass('instant').removeClass('visible');

						window.setTimeout(function() {

							slides[lastPos].removeClass('instant');
							isLocked = false;

						}, 100);

					}, options.speed);

			};

		// Indicators.
			if (options.indicators)
				$indicators = $('<ul class="indicators"></ul>').appendTo($this);

		// Slides.
			$slides
				.each(function() {

					var $slide = $(this),
						$img = $slide.find('img');

					// Slide.
						$slide
							.css('background-image', 'url("' + $img.attr('src') + '")')
							.css('background-position', ($slide.data('position') ? $slide.data('position') : 'center'));

					// Add to slides.
						slides.push($slide);

					// Indicators.
						if (options.indicators) {

							var $indicator_li = $('<li>' + i + '</li>').appendTo($indicators);

							// Indicator.
								$indicator_li
									.data('index', i)
									.on('click', function() {
										$this._switchTo($(this).data('index'), true);
									});

							// Add to indicators.
								indicators.push($indicator_li);

						}

					i++;

				})
				._parallax(options.parallax);

		// Initial slide.
			slides[pos].addClass('visible').addClass('top');

			if (options.indicators)
				indicators[pos].addClass('visible');

		// Bail if we only have a single slide.
			if (slides.length == 1)
				return;

		// Main loop.
			intervalId = window.setInterval(function() {

				current++;

				if (current >= slides.length)
					current = 0;

				$this._switchTo(current);

			}, options.delay);

	};

	$(function() {

		var	$window 	= $(window),
			$body 		= $('body'),
			$header 	= $('#header'),
			$banner 	= $('.banner');

		// Disable animations/transitions until the page has loaded.
			$body.addClass('is-loading');

			$window.on('load', function() {
				window.setTimeout(function() {
					$body.removeClass('is-loading');
				}, 100);
			});

		// Prioritize "important" elements on medium.
			skel.on('+medium -medium', function() {
				$.prioritize(
					'.important\\28 medium\\29',
					skel.breakpoint('medium').active
				);
			});

		// Banner.
			$banner._slider(settings.banner);

		// Menu.
			$('#menu')
				.append('<a href="#menu" class="close"></a>')
				.appendTo($body)
				.panel({
					delay: 500,
					hideOnClick: true,
					hideOnSwipe: true,
					resetScroll: true,
					resetForms: true,
					side: 'right'
				});

		// Header.
			if (skel.vars.IEVersion < 9)
				$header.removeClass('alt');

			if ($banner.length > 0
			&&	$header.hasClass('alt')) {

				$window.on('resize', function() { $window.trigger('scroll'); });

				$banner.scrollex({
					bottom:		$header.outerHeight(),
					terminate:	function() { $header.removeClass('alt'); },
					enter:		function() { $header.addClass('alt'); },
					leave:		function() { $header.removeClass('alt'); $header.addClass('reveal'); }
				});

			}

	});

});


/* 메인 슬라이드 */

var swiper = new Swiper('.swiper-container', {
	speed:1000,
	spaceBetween: 30, //사이간격
	slidesPerView: '4', //몇개씩 보여지게
	loop : true,
	centeredslides: true,
	slidesPerGroup : 1,
	loopAdditionalSlides : 1,
	grabCursor: false,
	loopFillGroupWithBlank: true,
	observer: true,
    observeParents: true,
	pagination : {
		el : ".swiper-pagination",
		clickable : true
	},
	navigation : {
	  nextEl: '.swiper-button-next',
	  prevEl: '.swiper-button-prev',
	},
	autoplay : {
	  delay: 2500,
	  disableOnInteraction: false,
	},
  });


	function openTab(event, tabName){
		// 모든 탭 콘텐츠를 숨김.
		var contents = document.query.SelectorAll('.tab-content');
		
		for(var i = 0; i < contents.length; i++){
			contents[i].classList.remove('active');
		}
		
		// 모든 탭을 비활성화 합니다.
		var tabs = document.querySelectorAll('.tab');
		for(var i = 0; i <tabs.length; i++){
			tabs[i].classList.remove('active');
		}
		
		//선택탄 탭을 활성화.
		document.getElementById(tabName).calssList.add('active');
		event.currentTarget.classList.add('active');
	}
	
	document.querySelector('.tab').click();
		
	
	$(documnet).ready(function (){
		$(".mySlideDiv").not(".active").hied();
		
		setInterval(nextSlide, 4000);
	});
	
	function prevSlide(){
		$(".mySlideDiv").hide();
		var allSlide = $(".mySlideDiv");
		
		var currentIndex = 0;
		
		$(".mySlideDiv").each(function(index,item){
			if($(this).hasClass("active")){
				currentIndex = index;
			}	
		});
		
		var newIndex = 0;
		
		if(currentIndex <= 0){
			
			nexIndex = allSlide.length - 1;
		}else{
			ndexIndex = currentIndex - 1;
		}
		
		$(".mySlideDiv").removeClass("actice");
		
		$(".mySlideDiv").eq(newIndex).addClass("active");
			$(".mySlideDiv").eq(newIndex).show();
	}
	
	// 다음 슬라이드
	function nextSlide(){
		$(".mySlideDiv").hide();
		var allSlide = $(".mySlideDiv");
		var currentIndex = 0;
		
		$(".mySlideDiv").each(function(index,item){
			if($(this).hasClass("active")){
					currentIndex = index;
			}
		});
		
		var newIndex = 0;
		
		if(currentIndex >= allSlide.length -1){
			// 현재 슬라이드 index가 마지막 순서면 0번쨰로 보냄
			newIndex = 0;
		}else{
			//현재 슬라이드의 index에서 한칸 만큼 앞으로
			newIndex = currentIndex + 1;
		}
		
		$(".mySlideDiv").removeClass("active");
		
		$(".mySlideDiv").eq(newIndex).addClass("active");
			$(".mySlideDiv").eq(newIndex).show();
			
	} 
	
	
	
	// 로그인 화면
	
	const signUpBtn = document.getElementById("signUP");
	
	const signInBtn = documnet.getElementById("signIn");
	
	const container = documnet.querySelector(".container");
	
	signUpBtn.addEventListener("click", () => {
		container.classList.add("right-panel-active");
	});
	
	signInBtn.addEventListener("click", () =>{
		container.classList.remove("right-panel-active");
	});
	
	// 회원가입 유효성검사
	// 자원을 화면에 로드하게 되면 수행할 동작(==function)
	
	window.onload =function(){
		var join = documnet.join; // form데이터를 모두 join변수에 저장
		
		// 유호성검사할 부분을 class로 부여했기에 check class 태그를 모두 input에 저장 가져옴.
		// 이때 input 한 태그당 배열 인덱스로 받는다.
		
		var input = documnet.querySelectorAll('.check');
		
		// 오류 문구 // errorId : span 의 id들(각 요소마다 나타낼 오류를 표시하기 위함)
		// error : class list의 하위 span을 모두 불러냄(일괄 처리를 위함 - 반복문)
		
		var errorId = ["idError", "pwError", "pwCheckError", "nameError", "phoneError", "emailError"];
		var error = documnet.querySellectAll('.list > span');
		
		// 오류문구 초기화 메서드
		// 오류 표시 후, 사용자가 올바르게 수정을 하면 텍스트가 사라지는 모습을 구현
		
		function innerReset(error){
			for(var i = 0; i < error.length; i++){
				error[i],innerHTML = "";
			}
		}
		
		// 초기화 메서드 호출
		innerReset(error);
		
		//[ID 입력문자 유호성검사]
		join.id.onkeydown = function(){
			innerReset(errow); // 초기화 메서드 호출
			var idLimit = /^[a-zA-Z0-9-_]{5,20}$/; // 정규식 5-20자 (a-z, A-Z, 0-9, -, _ 만 입력가능)
			
			if(!ldLimit.text(input[0],value)){ //입력값과 정규식 범위와 같지 않으면
				// id의 오류 문구 삽입
				documnet.getElemnetById(erroeId[0]).innerHTML = "5~20자의 영문 대소문자, 숫자와 특수기호(-),(_)만 사용 가능합니다. "
			}	
		}
		
		// [pw 입력문자 유효성 감사]
		join.pw.onkeydown = function(){
			innerReset(error); // 초기화 메서드 호출
			var pwLimit = /^[a-zA-Z0-9~!@#$%^&*()_-]{10,20}$/;
			
			if(!pwLimit.test(input[1],value)){
				documnet.getElementById(errorId[1]).innerHTML = "10~20자의 영문 대소문자 숫자와 특수기호 '~!@#$%^&*()_-'만 사용 가능합니다.";
			}
		}
		
		// pw 재확인 입력문자 초기화
		// 비밀번호 동일여부는 submit 버튼 클릭시 검사 해줄 예정
		join.pwchekc.onkeydown = function(){
			// pw의 오류 문구삽입
			innerReset(erroe); // 오류문구 초기화

		}
		
		// 휴대폰번호 입력문자 유호성검사
		join.phone.onkeydown =function(){ //입력값과 정규식 범위와 같지 않다면
		innerReset(error); // 초기화 메서드 호출
			var pnumLimit = /^01[0|1|6|7|8|9|]{1}[0-9]{8}$/; // 정규식
			
			if(!pnumLimit.text(input[4].value)){ // 입력값과 정규식 범위과 같지 않다면
					// pw의 오류 문구 삽입
					document.getElementById(errorId[4]).innerHTML = " 올바른 형식이 아닙니다." 				
			}
			
		}
		
		// 이메일 입력 유효성 검사
		join.email.onkeydown = function(){
			innerReset(error); // 초기화 메서드 호출
			var emailLimit = /[0-9a-zA-Z-_.]/; // 정규식
				if(!emailLimit.text(input[5].value)){
					document.getElemetById(errorId[5]).innerHTML = " 올바른 형식이 아닙니다."
				}
		}
	}
	
	// 출생년도 셀렉트박스  option 목록 동적 생성
	
	var date = new date();
	
	const birthYearEl = document.querySelector('#birth-year')
	// option 목록 생성 여부 확인
	isYearOptionExisted = false;
	birthYearEl.addEventListener('focus', function(){
		// year 목록 생성되지 않았을떄 (최초클릭시)
		if(!isYearOptionExisted){
			isYearOptionExisted = true
			for(var i = 1940; i <= date(); i++){
				// option element 생성
				const YearOption = document.createElement('option')
				YearOption.setAttribute('value', i )
				YearOption.innerText = i
				// birthYearEl 의 자식 요소로 추가
				this.appendChild(YearOption);
			}
		}
	})
	
	const birthMonthEl = document.querySelector('#birth-month')
	
	isMonthOptionExisted = false;
	birthMonthEl.addEventListener('focus', function(){
		if(!isMonthOptionExisted){
			isMonthOptionExisted = true
				for(var i = 1; i <=12; i++){
					const MonthOption = document.createElement('option')
					MonthOption.setAttribute('value', i)
					MonthOption.innerText = i
					
					this.appendChild(MonthOption);
				}
		}
	})
	
	const birthDayEl = document.querySelector('#birth-day')
	
	isDayOptionExisted = false;
	birthDayEl.addEventListener('focus', function(){
		if(!isDayOptionExisted){
				isDayOptionExisted= true
					for(var i = 1; i <=31; i++){
						const DayOption = documnet.createElement('option')
						DayOption.setAttribute('value', i)
						DayOption.innerText = i
						
						this.appendChild(DayOption);
					}
		}
	});
	
	
	
	// 메인 배너
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	