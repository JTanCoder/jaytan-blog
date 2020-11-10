$(function(){
	$(".nav>ul>li").hover(function(){
		$(this).children("a").css("border-bottom","2px solid #00a0e9");
		$(this).children("a").css("color","#00a0e9");
		$(this).find(".covering").show();
		$(this).find("ul").stop().fadeIn(300);
	},function(){
		$(this).children("a").css("border-bottom","");
		$(this).children("a").css("color","");
		$(this).find(".covering").hide();
		$(this).find("ul").stop().fadeOut(300);
	});
	$(".header_information>div").hover(function(){
		$(this).find("div").stop().fadeIn(300);
	},function(){
		$(this).find("div").stop().fadeOut(300);
	});
	$(".suspension_a").click(function() {
        $('body,html').animate({
          scrollTop: 0
        },
        1000);
        return false;
    });
});